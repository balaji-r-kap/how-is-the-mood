package com.kapture.howisthemood.controller;

import com.kapture.howisthemood.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/get-all-events")
    public ResponseEntity<?> getAllEvents() {
        return calendarService.getAllEvents();
    }

}
