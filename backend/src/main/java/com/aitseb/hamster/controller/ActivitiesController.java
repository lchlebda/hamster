package com.aitseb.hamster.controller;

import com.aitseb.hamster.dto.Activity;
import com.aitseb.hamster.dto.StravaActivity;
import com.aitseb.hamster.repository.StravaActivitiesRepository;
import com.aitseb.hamster.utils.ActivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivitiesController {

    private final StravaActivitiesRepository stravaActivitiesRepository;

    @GetMapping
    public List<Activity> getActivities(@RequestHeader(name = "ACCESS_TOKEN") String accessToken) {
        List<StravaActivity> activities = stravaActivitiesRepository.getList(accessToken);
        return activities.stream()
                .map(ActivityMapper::fromStrava)
                .collect(toList());
    }
}
