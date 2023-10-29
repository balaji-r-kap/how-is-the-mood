package com.kapture.howisthemood.model;

import com.google.api.services.calendar.model.EventAttendee;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class InternalUser {

    @Id
    private String  emailAddress;
    private boolean watchSetupDone;

    public InternalUser(EventAttendee attendee) {
        this.emailAddress = attendee.getEmail();
        this.watchSetupDone = false;
    }
}
