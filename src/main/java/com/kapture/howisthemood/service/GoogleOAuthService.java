package com.kapture.howisthemood.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.kapture.howisthemood.constants.GoogleConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static com.kapture.howisthemood.constants.GoogleConstants.CREDENTIALS_FILE_PATH;
import static com.kapture.howisthemood.constants.GoogleConstants.JSON_FACTORY;
import static com.kapture.howisthemood.constants.GoogleConstants.SCOPES;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GoogleOAuthService {

    private final HttpServletResponse response;
    private final HttpServletRequest  request;
    GoogleAuthorizationCodeFlow flow;

    public static String getAuthTokenFromCookies(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("google_oauth_token")) {
                        return cookie.getValue();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setAuthTokenInCookies(HttpServletResponse response, String authToken) {
        Cookie cookie = new Cookie("google_oauth_token", authToken);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 12);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public void initOAuth(HttpServletResponse response) throws IOException {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            // Load client secrets.
            InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
            }
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(GoogleConstants.TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            String authorizationUrl = flow.newAuthorizationUrl()
                    .setRedirectUri(GoogleConstants.CALLBACK_URL)
                    .build();
            response.sendRedirect(authorizationUrl);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Error");
        }
    }

    public void handleCallback(String code) {
        try {
            TokenResponse tokenResp = flow.newTokenRequest(code).setRedirectUri(GoogleConstants.CALLBACK_URL).execute();
            Credential credential = flow.createAndStoreCredential(tokenResp, "userID");
            String authToken = credential.getAccessToken();
            GoogleOAuthService.setAuthTokenInCookies(response, authToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
