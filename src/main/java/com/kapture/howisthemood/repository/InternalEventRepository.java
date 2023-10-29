package com.kapture.howisthemood.repository;

import com.kapture.howisthemood.model.InternalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalEventRepository extends JpaRepository<InternalEvent, String> {
}
