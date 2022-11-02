package com.aitseb.hamster.controller;

import com.aitseb.hamster.dao.Activity;
import com.aitseb.hamster.dto.ActivityDTO;
import com.aitseb.hamster.dto.StravaActivity;
import com.aitseb.hamster.repository.ActivitiesRepository;
import com.aitseb.hamster.repository.StravaActivitiesRepository;
import com.aitseb.hamster.utils.ActivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivitiesController {

    private final StravaActivitiesRepository stravaActivitiesRepository;
    private final ActivitiesRepository activitiesRepository;

    @GetMapping
    public List<ActivityDTO> getActivities(@RequestHeader(name = "ACCESS_TOKEN") String accessToken) {
        getAndSaveLatestStravaActivities(accessToken);

        return ((List<Activity>) activitiesRepository.findAll())
                .stream()
                .sorted(comparing(Activity::getDate).reversed())
                .map(ActivityMapper::fromDAOToDTO)
                .collect(toList());
    }

    @PostMapping("/update")
    public boolean updateActivity(@RequestParam long id, @RequestParam String prop, @RequestParam String value) {
        switch (prop) {
            case "time" -> activitiesRepository.updateTime(id, Integer.valueOf(value));
            case "title" -> activitiesRepository.updateDescription(id, value);
        }
        return true;
    }

    @GetMapping("/getAll")
    public List<Activity> getActivities() {
        return (List<Activity>) activitiesRepository.findAll();
    }

    private void getAndSaveLatestStravaActivities(String accessToken) {
        Activity activity = activitiesRepository.findFirstByOrderByDateDesc();
        long timestamp = Timestamp.valueOf(activity.getDate()).getTime()/1000;

        List<StravaActivity> activities = stravaActivitiesRepository.getList(accessToken, timestamp);
        activities.stream()
                .filter(stravaActivity -> stravaActivity.id() != activity.getStravaId())
                .forEach(this::saveActivity);
    }

    private void saveActivity(StravaActivity stravaActivity) {
        activitiesRepository.save(ActivityMapper.fromStravaToDAO(stravaActivity));
    }

}
