package com.aitseb.hamster.controller;

import com.aitseb.hamster.dto.StravaActivity;
import com.aitseb.hamster.dto.StravaActivityType;
import com.aitseb.hamster.repository.StravaActivitiesRepository;
import com.aitseb.hamster.service.StravaAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Comparator.comparing;

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
        String inputFilePath = hamsterDir + "/backend/src/main/resources/" + "data_output.csv";
        String outputFilePath = hamsterDir + "/backend/src/main/resources/" + "data_output1.csv";

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

}
