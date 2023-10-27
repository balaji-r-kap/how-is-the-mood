package com.kapture.howisthemood.controller;

import com.kapture.howisthemood.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/google")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    @GetMapping("/init")
    public ResponseEntity<?> initGoogleOAuth(){
        return googleAuthService.initOAuth();
    }

}
