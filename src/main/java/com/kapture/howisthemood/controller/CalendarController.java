package com.kapture.howisthemood.controller;

import com.kapture.howisthemood.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/calendar/get-all-events")
    public ResponseEntity<?> getAllEvents() {
        return calendarService.getAllEvents();
    }

    @PostMapping("/callback/calendar/events/watch")
    public void watchCalendar(HttpServletRequest request) {
        System.out.println("====================== push notification has come! ======================");
        calendarService.handleCalendarWatch(request);
    }

}
