package com.aitseb.hamster.controller;

import com.aitseb.hamster.dao.Activity;
import com.aitseb.hamster.dto.*;
import com.aitseb.hamster.exception.StravaException;
import com.aitseb.hamster.repository.ActivitiesRepository;
import com.aitseb.hamster.repository.StravaActivitiesRepository;
import com.aitseb.hamster.utils.ActivityMapper;
import com.aitseb.hamster.utils.DateHelper;
import com.aitseb.hamster.utils.Units;
import lombok.RequiredArgsConstructor;
import org.decimal4j.util.DoubleRounder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivitiesController {

    private final StravaActivitiesRepository stravaActivitiesRepository;
    private final ActivitiesRepository activitiesRepository;

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getActivities(@RequestHeader(name = "ACCESS_TOKEN") String accessToken) {
        boolean stravaWorks = true;
        try {
            getAndSaveLatestStravaActivities(accessToken);
        } catch (StravaException exc) {
            stravaWorks = false;
        }

        List<ActivityDTO> list = ((List<Activity>) activitiesRepository.findAll())
                                                    .stream()
                                                    .sorted(comparing(Activity::getDate).reversed())
                                                    .map(ActivityMapper::fromDAOToDTO)
                                                    .collect(toList());

        return stravaWorks ? ResponseEntity.ok(list)
                           : ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(list);
    }

    @GetMapping("/year/{year}/perWeek")
    public ResponseEntity<WeekViewDTO> getActivitiesPerWeek(@RequestHeader(name = "ACCESS_TOKEN") String accessToken,
                                                            @PathVariable int year) {
        boolean stravaWorks = true;
        try {
            getAndSaveLatestStravaActivities(accessToken);
        } catch (StravaException exc) {
            stravaWorks = false;
        }

        List<ActivityDTO> activities = ((List<Activity>) activitiesRepository.findAll())
                .stream()
                .filter(activity -> activity.getDate().toLocalDate().isAfter(DateHelper.weekViewYearStartsAfter(year)) &&
                                    activity.getDate().toLocalDate().isBefore(DateHelper.weekViewYearEndsBefore(year)))
                .sorted(comparing(Activity::getDate).reversed())
                .map(ActivityMapper::fromDAOToDTO)
                .collect(toList());

        List<WeekSummary> weekSummaryList = activities.stream().collect(
                Collectors.collectingAndThen(
                        groupingBy(ActivityDTO::yearWeekKey),
                        l -> l.values().stream()
                                .map(ActivityMapper::createWeekSummary)
                                .sorted(comparing(WeekSummary::year).thenComparing(WeekSummary::weekOfYear).reversed())
                                .collect(toList())
                ));

        WeekViewDTO weekViewDTO = new WeekViewDTO(activities, weekSummaryList);

        return stravaWorks ? ResponseEntity.ok(weekViewDTO)
                : ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(weekViewDTO);
    }

    @GetMapping("/year/{year}/week/{week}/summary/{prop}")
    public ResponseEntity<Double> getWeekSummaryForProp(
            @PathVariable String prop,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        double sum = ((List<Activity>) activitiesRepository.findAll())
                .stream()
                .filter(activity -> activity.getDate().toLocalDate().isAfter(date.minusDays(date.getDayOfWeek().getValue())) &&
                        activity.getDate().toLocalDate().isBefore(date.plusDays(7-date.getDayOfWeek().ordinal())))
                .mapToDouble(activity -> getValueForProp(activity, prop))
                .sum();

        return ResponseEntity.ok(getSumValueForProp(sum, prop));
    }

    @PostMapping("/update/{id}")
    public boolean updateActivity(@PathVariable long id,
                                  @RequestParam StravaActivityType type,
                                  @RequestParam String prop,
                                  @RequestParam String value) {
        switch (prop) {
            case "title" -> activitiesRepository.updateDescription(id, value);
            case "time" -> activitiesRepository.updateTime(id, Integer.valueOf(value));
            case "regeTime" -> activitiesRepository.updateRegeTime(id, Integer.valueOf(value));
            case "hr" -> activitiesRepository.updateHr(id, Integer.valueOf(value));
            case "hrMax" -> activitiesRepository.updateHrMax(id, Integer.valueOf(value));
            case "cadence" -> activitiesRepository.updateCadence(id, Integer.valueOf(value));
            case "power" -> activitiesRepository.updatePower(id, Integer.valueOf(value));
            case "ef" -> activitiesRepository.updateEf(id, Float.valueOf(value));
            case "tss" -> activitiesRepository.updateTSS(id, Float.valueOf(value));
            case "effort" -> activitiesRepository.updateEffort(id, Integer.valueOf(value));
            case "elevation" -> activitiesRepository.updateElevation(id, Integer.valueOf(value));
            case "speed" -> activitiesRepository.updateSpeed(id, parseSpeed(type, value));
            case "distance" -> activitiesRepository.updateDistance(id, parseDistance(type, value));
            case "notes" -> activitiesRepository.updateNotes(id, value);
        }

        return true;
    }

    @DeleteMapping("/delete/{id}")
    public void deleteActivity(@PathVariable long id) {
        activitiesRepository.deleteById(id);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Activity>> getActivities() {
        return ResponseEntity.ok((List<Activity>) activitiesRepository.findAll());
    }

    private void getAndSaveLatestStravaActivities(String accessToken) {
        Activity activity = activitiesRepository.findFirstByOrderByDateDesc();
        long timestamp = Timestamp.valueOf(activity.getDate()).getTime()/1000;

        List<StravaActivity> activities = stravaActivitiesRepository.getListAfter(accessToken, timestamp);
        activities.stream()
                .filter(stravaActivity -> stravaActivity.id() != activity.getStravaId())
                .forEach(this::saveActivity);
    }

    private void saveActivity(StravaActivity stravaActivity) {
        activitiesRepository.save(ActivityMapper.fromStravaToDAO(stravaActivity));
    }

    private float parseSpeed(StravaActivityType type, String value) {
        switch (type) {
            case Run -> { return Units.runPaceToMs(value); }
            case Ride -> { return Units.kmhToMs(value); }
            case Swim -> { return Units.swimPaceToMs(value); }
        }
        throw new IllegalArgumentException("Speed shouldn't be set for " + type);
    }

    private int parseDistance(StravaActivityType type, String value) {
        switch (type) {
            case Run, Ride, Hike, Walk, AlpineSki, BackcountrySki -> { return Units.parseDistanceInKmToMetres(value); }
            case Swim -> { return Units.parseDistanceInMetres(value); }
        }
        throw new IllegalArgumentException("Distance shouldn't be set for " + type);
    }

    private double getValueForProp(Activity activity, String prop) {
        switch (prop) {
            case "time" -> { return activity.getTime(); }
            case "regeTime" -> { return activity.getRegeTime(); }
            case "effort" -> { return activity.getEffort(); }
            case "tss" -> { return activity.getTss(); }
            case "elevation" -> { return activity.getElevation(); }
            case "distance" -> { return activity.getDistance(); }
            default -> { return 0; }
        }
    }

    private double getSumValueForProp(double sum, String prop) {
        switch (prop) {
            case "time", "regeTime" -> { return DoubleRounder.round((float)sum/60, 2); }
            case "tss" -> { return DoubleRounder.round((float)sum, 2); }
            case "effort", "elevation" -> { return sum; }
            case "distance" -> { return DoubleRounder.round((float) sum/1000, 1); }
            default -> { return 0; }
        }
    }

}
