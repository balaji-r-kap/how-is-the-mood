package com.kapture.howisthemood.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Profile;
import com.kapture.howisthemood.model.InternalEvent;
import com.kapture.howisthemood.repository.InternalEventRepository;
import com.kapture.howisthemood.repository.InternalUserRepository;
import com.kapture.howisthemood.model.InternalUser;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.kapture.howisthemood.constants.GoogleConstants.APPLICATION_NAME;
import static com.kapture.howisthemood.constants.GoogleConstants.JSON_FACTORY;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CalendarService {

    private final HttpServletRequest      request;
    private final BaseResponse            baseResponse;
    private final InternalUserRepository  internalUserRepository;
    private final GoogleOAuthService      GoogleOAuthService;
    private final InternalEventRepository internalEventRepository;
    private final InternalEventService    internalEventService;
    private final JobScheduleService      jobScheduleService;

    @Value("${callback.baseurl}")
    private String callbackBaseUrl;

    public ResponseEntity<?> getAllEvents() {
        try {
            Credential credential = (Credential) request.getAttribute("credential");
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = service.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            return baseResponse.successResponse(items);
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 401) {
                return GoogleOAuthService.initOAuth(request.getServletPath());
            }
            return baseResponse.errorResponse(e);
        } catch (Exception e) {
            return baseResponse.errorResponse(e);
        }
    }

    public void handleCalendarWatch(HttpServletRequest request) {
        try {
            Credential credential = (Credential) request.getAttribute("credential");
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = service.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setTimeMax(new DateTime(86_400_000L + System.currentTimeMillis()))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> eventList = events.getItems();
            eventList.forEach(item -> {
                Optional<InternalEvent> storedEventOp = internalEventRepository.findById(item.getId());
                InternalEvent storedEvent = storedEventOp.orElse(null);
                InternalEvent eventItem = internalEventService.getInternalEvent(item);
                if (!Objects.equals(eventItem, storedEvent)) {
                    eventItem.setJobScheduled(false);
                    internalEventRepository.saveAndFlush(eventItem);
                }
            });
            jobScheduleService.scheduleJobs();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean createCalenderEventsWatchChannel() {
        try {
            Credential credential = (Credential) request.getAttribute("credential");
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail gmailService = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("Your Application Name")
                    .build();
            Profile profile = gmailService.users().getProfile("me").execute();
            String emailAddress = profile.getEmailAddress();
            Optional<InternalUser> internalUser = internalUserRepository.findById(emailAddress);
            if (internalUser.isEmpty() || !internalUser.get().isWatchSetupDone()) {
                InternalUser user = new InternalUser();
                user.setEmailAddress(emailAddress);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Authorization", "Bearer " + credential.getAccessToken());
                try {
                    String url = "https://www.googleapis.com/calendar/v3/calendars/" + emailAddress + "/events/watch";
                    Map<String, Object> reqBody = new HashMap<String, Object>();
                    reqBody.put("id", UUID.randomUUID().toString());
                    reqBody.put("type", "web_hook");
                    reqBody.put("address", callbackBaseUrl + "/calendar/events/watch");
                    user.setWatchSetupDone(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                internalUserRepository.saveAndFlush(user);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
