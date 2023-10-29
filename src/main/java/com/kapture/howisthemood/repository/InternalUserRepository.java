package com.kapture.howisthemood.repository;

import com.kapture.howisthemood.model.InternalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalUserRepository extends JpaRepository<InternalUser, String> {

}
