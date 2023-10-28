package com.kapture.howisthemood.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.kapture.howisthemood.constants.GoogleConstants.APPLICATION_NAME;
import static com.kapture.howisthemood.constants.GoogleConstants.JSON_FACTORY;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CalendarService {

    private final HttpServletRequest request;
    private final BaseResponse       baseResponse;

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
        } catch (Exception e) {
            return baseResponse.errorResponse(e);
        }
    }
}
