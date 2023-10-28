package com.kapture.howisthemood.controller;

import com.kapture.howisthemood.service.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    @GetMapping("/init")
    public ResponseEntity<?> init(@RequestParam(name = "return", defaultValue = "/") String returnUrl) {
        return googleOAuthService.initOAuth(returnUrl);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code) {
        googleOAuthService.handleCallback(code);
    }

    @GetMapping("/user")
    public Principal getUser(Principal principal) {
        return principal;
    }

}