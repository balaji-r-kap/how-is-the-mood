package com.kapture.howisthemood.constants;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Collections;
import java.util.List;

public class GoogleConstants {
    public static final String      APPLICATION_NAME      = "Google Calendar API Java Quickstart";
    /**
     * Global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY          = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    public static final String      TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    public static final List<String> SCOPES                =
            Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    public static final String       CREDENTIALS_FILE_PATH = "/credentials.json";
    public static final String       CALLBACK_URL          = "http://localhost:7001/auth/callback";
}
