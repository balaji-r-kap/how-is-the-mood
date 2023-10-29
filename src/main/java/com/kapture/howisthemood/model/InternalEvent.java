package com.kapture.howisthemood.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class InternalEvent {

    @Id
    private String id;

    @ManyToMany
    private List<InternalUser> attendees;

    private Timestamp startTime;

    private Timestamp endTime;

    private boolean jobScheduled;

    @Column(length = 5026)
    private String description;

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        InternalEvent that = (InternalEvent) object;
        return jobScheduled == that.jobScheduled && Objects.equals(id, that.id)
                //                && Objects.equals(attendees, that.attendees)
                && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
