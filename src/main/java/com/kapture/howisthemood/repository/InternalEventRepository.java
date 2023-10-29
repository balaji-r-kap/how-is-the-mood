package com.kapture.howisthemood.repository;

import com.kapture.howisthemood.model.InternalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternalEventRepository extends JpaRepository<InternalEvent, String> {
    List<InternalEvent> findAllByJobScheduled(boolean jobScheduled);
}
