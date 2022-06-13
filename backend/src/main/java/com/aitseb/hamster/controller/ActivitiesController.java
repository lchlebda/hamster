package com.aitseb.hamster.controller;

import com.aitseb.hamster.dto.StravaActivity;
import com.aitseb.hamster.repository.StravaActivitiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivitiesController {

    private final StravaActivitiesRepository stravaActivitiesRepository;

    @GetMapping
    public List<StravaActivity> getActivities(@RequestHeader(name = "ACCESS_TOKEN") String accessToken) {
//        return List.of(new StravaActivity(10L, "Skitury", 180), new StravaActivity(20L, "Siłownia", 90));
        return stravaActivitiesRepository.getList(accessToken);
    }
}
