package com.kapture.howisthemood.service;

import com.google.api.services.calendar.model.Event;
import com.kapture.howisthemood.model.InternalEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InternalEventService {

    private final InternalUserService internalUserService;

    public InternalEvent getInternalEvent(Event event) {
        InternalEvent iEvent = new InternalEvent();
        try {
            iEvent.setId(event.getId());
            iEvent.setStartTime(new Timestamp(event.getStart().getDateTime().getValue()));
            iEvent.setEndTime(new Timestamp(event.getEnd().getDateTime().getValue()));
            iEvent.setDescription(event.getDescription());
            iEvent.setAttendees(event.getAttendees().stream().map(internalUserService::getInternalUser).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iEvent;
    }
}
