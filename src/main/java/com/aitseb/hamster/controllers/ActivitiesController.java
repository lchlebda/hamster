package com.aitseb.hamster.controllers;

import com.aitseb.hamster.dto.Activity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivitiesController {

    @GetMapping
    public List<Activity> getActivities() {
        return List.of(new Activity(10L, "Skitury", 180, 10), new Activity(20L, "Siłownia", 90, 100));
    }
}
