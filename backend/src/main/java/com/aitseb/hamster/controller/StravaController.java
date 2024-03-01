package com.aitseb.hamster.controller;

import com.aitseb.hamster.dao.Activity;
import com.aitseb.hamster.dto.StravaActivity;
import com.aitseb.hamster.dto.StravaActivityType;
import com.aitseb.hamster.repository.StravaActivitiesRepository;
import com.aitseb.hamster.service.StravaAuthorizationService;
import com.aitseb.hamster.utils.ActivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StravaController {

    private final StravaAuthorizationService stravaAuthorizationService;
    private final StravaActivitiesRepository stravaActivitiesRepository;

    @GetMapping("/strava/oauth")
    public String getToken(@RequestParam String clientId,
                           @RequestParam String clientSecret,
                           @RequestParam String code) {
        return stravaAuthorizationService.getToken(clientId, clientSecret, code);
    }

    @PostMapping("/updateStravaIds")
    public ResponseEntity<Boolean> updateStravaIds(@RequestHeader(name = "ACCESS_TOKEN") String accessToken) {
        String hamsterDir = System.getProperty("user.dir");
        String inputFilePath = hamsterDir + "/backend/src/main/resources/" + "Rozpiska2019.csv";
        String outputFilePath = hamsterDir + "/backend/src/main/resources/" + "data_output.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            String line;
            LocalDateTime lastDateFromStravaCall = LocalDateTime.of(2000, 1, 1, 1, 1);
            List<StravaActivity> activities = Collections.emptyList();
            while ((line = reader.readLine()) != null) {
                List<String> columns = new ArrayList<>(Arrays.asList(line.split("\\|", 4)));
                if (columns.get(0).trim().equals("0")) {
                    String date = columns.get(1).trim();
                    LocalDateTime currentDate = LocalDateTime.parse(date);
                    long after = Timestamp.valueOf(currentDate).getTime()/1000;
                    if (currentDate.isAfter(lastDateFromStravaCall)) {
                        activities = stravaActivitiesRepository.getListAfter(accessToken, after);
                        lastDateFromStravaCall = activities.stream()
                                .max(comparing(StravaActivity::start_date))
                                .get().start_date();
                    }
                    String currentType = columns.get(2).trim();
                    String stravaId = activities.stream()
                            .filter(activity -> activity.start_date().getDayOfMonth() == currentDate.getDayOfMonth())
                            .filter(activity -> activity.type().name().equals(currentType)
                                    || (currentType.equals(StravaActivityType.WeightTraining.name()) && activity.type().name().equals(StravaActivityType.Workout.name())))
                            .map(StravaActivity::id)
                            .findFirst().orElse(0L).toString();
                    columns.set(0, stravaId + " ");
                }
                String convertedLine = String.join("|", columns);
                writer.write(convertedLine);
                writer.newLine();
            }
            System.out.println("Conversion completed successfully.");
            return ResponseEntity.ok(true);

        }  catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error during conversion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/updateActivitiesFromStrava")
    public ResponseEntity<Boolean> updateActivitiesFromStrava(@RequestHeader(name = "ACCESS_TOKEN") String accessToken) {
        String hamsterDir = System.getProperty("user.dir");
        String outputFilePath = hamsterDir + "/backend/src/main/resources/" + "data_output1.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            LocalDateTime lastDateFromStravaCall = LocalDateTime.of(2019, 9, 22, 1, 1);
            List<StravaActivity> activities;
            while (lastDateFromStravaCall.isBefore(LocalDateTime.of(2019, 12, 2, 1, 1))) {
                long after = Timestamp.valueOf(lastDateFromStravaCall).getTime()/1000;
                activities = stravaActivitiesRepository.getListAfter(accessToken, after);
                activities.stream()
                        .map(this::fromStravaToDAO)
                        .filter(activity -> activity.getDate().isBefore(LocalDateTime.of(2019, 12, 2, 1, 1)))
                        .sorted(comparing(Activity::getDate))
                        .skip(1)
                        .map(this::mapActivityToColumns)
                        .forEach((columns) -> {
                            String convertedLine = String.join(" | ", columns);
                            try {
                                writer.write(convertedLine);
                                writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                lastDateFromStravaCall =  activities.stream()
                        .map(ActivityMapper::fromStravaToDAO)
                        .sorted(comparing(Activity::getDate, reverseOrder()))
                        .map(Activity::getDate)
                        .findFirst()
                        .orElse(lastDateFromStravaCall);
            }
            System.out.println("Update completed successfully.");
            return ResponseEntity.ok(true);

        }  catch (Throwable e) {
            e.printStackTrace();
            System.err.println("Error during conversion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    private Activity fromStravaToDAO(StravaActivity stravaActivity) {
        Activity activity = ActivityMapper.fromStravaToDAO(stravaActivity);
        activity.setDescription(stravaActivity.name());

        return activity;
    }

    private String[] mapActivityToColumns(Activity activity) {
        String[] columns = new String[17];
        columns[0] = Long.toString(activity.getStravaId());
        columns[1] = activity.getDate().toString();
        columns[2] = activity.getSport().name();
        columns[3] = activity.getDescription();
        columns[4] = Integer.toString(activity.getTime());
        columns[5] = Integer.toString(activity.getRegeTime());
        columns[6] = Integer.toString(activity.getHr());
        columns[7] = Integer.toString(activity.getHrMax());
        columns[8] = Integer.toString(activity.getCadence());
        columns[9] = Integer.toString(activity.getPower());
        columns[10] = Float.toString(activity.getEf());
        columns[11] = Float.toString(activity.getTss());
        columns[12] = Integer.toString(activity.getEffort());
        columns[13] = Integer.toString(activity.getElevation());
        columns[14] = Float.toString(activity.getSpeed());
        columns[15] = Integer.toString(activity.getDistance());
        columns[16] = activity.getNotes() == null ? "" : activity.getNotes();

        return columns;
    }


}
