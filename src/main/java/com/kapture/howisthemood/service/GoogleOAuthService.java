package com.kapture.howisthemood.service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.kapture.howisthemood.constants.GoogleConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static com.kapture.howisthemood.constants.GoogleConstants.CREDENTIALS_FILE_PATH;
import static com.kapture.howisthemood.constants.GoogleConstants.JSON_FACTORY;
import static com.kapture.howisthemood.constants.GoogleConstants.SCOPES;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GoogleOAuthService {

    private final HttpServletResponse response;
    private final HttpServletRequest  request;
    private final BaseResponse        baseResponse;

    private GoogleAuthorizationCodeFlow flow;

    @Value("${google.apis.client.id}")
    private String clientId;

    @Value("${google.apis.client.secret}")
    private String clientSecret;

    public Credential getCredentialFromCookies() {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("access_token")) {
                        String accessToken = cookie.getValue();
                        if (accessToken != null) {
                            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                                    .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                                    .build();
                            credential.setAccessToken(accessToken);
                            return credential;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setCredentialInCookies(HttpServletResponse response, Credential credential) {
        try {
            Cookie cookie = new Cookie("access_token", credential.getAccessToken());
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 12);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<?> initOAuth(String returnUrl) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
            }
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .build();
            String redirectUrl = GoogleConstants.CALLBACK_URL;
            String authorizationUrl = flow.newAuthorizationUrl()
                    .setRedirectUri(redirectUrl)
                    .build();
            var data = Map.of("returnUrl", returnUrl,
                    "authUrl", authorizationUrl);
            return baseResponse.successResponse(data, "Open the authUrl to login!");
        } catch (Exception e) {
            e.printStackTrace();
            return baseResponse.errorResponse(e);
        }
    }

    public void handleCallback(String code) {
        try {
            TokenResponse tokenResp = flow.newTokenRequest(code).setRedirectUri(GoogleConstants.CALLBACK_URL).execute();
            Credential credential = flow.createAndStoreCredential(tokenResp, "userID");
            GoogleOAuthService.setCredentialInCookies(response, credential);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
