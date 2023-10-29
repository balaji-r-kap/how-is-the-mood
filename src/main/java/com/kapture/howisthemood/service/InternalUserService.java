package com.kapture.howisthemood.service;

import com.google.api.services.calendar.model.EventAttendee;
import com.kapture.howisthemood.model.InternalUser;
import com.kapture.howisthemood.repository.InternalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InternalUserService {

    private final InternalUserRepository internalUserRepository;

    public InternalUser getInternalUser(EventAttendee attendee) {
        try {
            Optional<InternalUser> userOp = internalUserRepository.findById(attendee.getEmail());
            if (userOp.isPresent()) {
                return userOp.get();
            }
            InternalUser user = new InternalUser(attendee);
            return internalUserRepository.saveAndFlush(user);
        } catch (Exception e) {
            return new InternalUser();
        }
    }

}
