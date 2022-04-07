package com.aitseb.hamster.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivitiesController {

    @GetMapping
    public List<String> getActivities() {
        return List.of("Activity");
    }
}
