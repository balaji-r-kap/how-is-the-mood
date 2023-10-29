package com.kapture.howisthemood.constants;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.gmail.GmailScopes;

import java.util.List;

public class GoogleConstants {
    public static final String       APPLICATION_NAME      = "How Is The Mood";
    public static final JsonFactory  JSON_FACTORY          = GsonFactory.getDefaultInstance();
    public static final String       TOKENS_DIRECTORY_PATH = "tokens";
    public static final List<String> SCOPES                = List.of(CalendarScopes.CALENDAR);
    public static final String       CREDENTIALS_FILE_PATH = "/credentials.json";
    public static final String       CALLBACK_URL          = "http://localhost:7001/auth/callback";
}
