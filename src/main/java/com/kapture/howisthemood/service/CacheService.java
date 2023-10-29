package com.kapture.howisthemood.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    public void putAccessToken(String emailAddress, String accessToken) {
        hashOperations.put("EMAIL_TO_ACCESS_TOKEN_MAP", emailAddress, accessToken);
    }

    public String getAccessToken(String emailAddress) {
        try {
            return (String) hashOperations.get("EMAIL_TO_ACCESS_TOKEN_MAP", emailAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

