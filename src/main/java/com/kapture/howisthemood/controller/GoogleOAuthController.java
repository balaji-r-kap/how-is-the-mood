package com.kapture.howisthemood.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.kapture.howisthemood.constants.GoogleConstants;
import com.kapture.howisthemood.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GoogleOAuthController {

    private final GoogleAuthService googleAuthService;

    @GetMapping
    public String welcome() {
        return "Welcome to how is the mood";
    }

    @GetMapping("/user")
    public Principal getUser(Principal principal){
        System.out.println(principal);
        return principal;
    }

    @GetMapping("/authorize")
    public String authorize() {
        // Redirect users to Google's authorization URL
        String authorizationUrl = "https://accounts.google.com/o/oauth2/auth" +
                "?client_id=" + "282990598139-jnp9fmg1m882m0fc6q388k7g7u2krnug.apps.googleusercontent.com" +
                "&redirect_uri=http://localhost:8080/callback" +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/userinfo.profile";
        return "Redirect to: " + authorizationUrl;
    }

    @GetMapping("/callback")
    public String callback(String code) {
        // Use the code received from Google to obtain an access token
        // You'll need to make an HTTP POST request to Google's token endpoint here
        // and handle the response to obtain the access token.
        // For security and best practices, consider using a library like Spring Security OAuth.
        System.out.println(code);
        return "Received authorization code: " + code;
    }
}