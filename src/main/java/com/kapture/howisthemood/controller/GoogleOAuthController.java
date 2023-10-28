package com.kapture.howisthemood.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.kapture.howisthemood.service.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GoogleOAuthController {

    private final GoogleOAuthService          googleOAuthService;
    private       GoogleAuthorizationCodeFlow flow;

    @GetMapping("/init")
    public void init(HttpServletResponse response) throws IOException {
        googleOAuthService.initOAuth(response);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam(value = "code") String code) {
        try {
            googleOAuthService.handleCallback(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/user")
    public Principal getUser(Principal principal) {
        return principal;
    }

}