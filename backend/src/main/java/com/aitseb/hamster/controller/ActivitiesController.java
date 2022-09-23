package com.aitseb.hamster.controller;

import com.aitseb.hamster.dao.Activity;
import com.aitseb.hamster.dto.ActivityDTO;
import com.aitseb.hamster.dto.StravaActivity;
import com.aitseb.hamster.repository.ActivitiesRepository;
import com.aitseb.hamster.repository.StravaActivitiesRepository;
import com.aitseb.hamster.service.ActivitiesService;
import com.aitseb.hamster.utils.ActivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivitiesController {

    private final StravaActivitiesRepository stravaActivitiesRepository;
    private final ActivitiesService activitiesService;
    private final ActivitiesRepository activitiesRepository;

    @GetMapping
    public List<ActivityDTO> getActivities(@RequestHeader(name = "ACCESS_TOKEN") String accessToken) {
        List<StravaActivity> activities = stravaActivitiesRepository.getList(accessToken);
        activities.forEach(activitiesService::save);
        return activities.stream()
                .map(ActivityMapper::fromStravaToDTO)
                .collect(toList());
    }

    @GetMapping("/getAll")
    public List<Activity> getActivities() {
        return (List<Activity>) activitiesRepository.findAll();
    }

}
