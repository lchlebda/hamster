package com.aitseb.hamster.controller;

import com.aitseb.hamster.service.StravaAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StravaController {

    private final StravaAuthorizationService stravaAuthorizationService;

    @GetMapping("/strava/oauth")
    public String getToken(@RequestParam String clientId,
                           @RequestParam String clientSecret,
                           @RequestParam String code) {
        return stravaAuthorizationService.getToken(clientId, clientSecret, code);
    }

    @PostMapping("/updateStravaIds")
    public ResponseEntity<Boolean> updateStravaIds(@RequestHeader(name = "ACCESS_TOKEN") String accessToken) {
        String hamsterDir = System.getProperty("user.dir");
        String inputFilePath = hamsterDir + "/backend/src/main/resources/" + "data.csv";
        String outputFilePath = hamsterDir + "/backend/src/main/resources/" + "data_output.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> columns = new ArrayList<>(Arrays.asList(line.split("\\|", 2)));
                if (columns.get(0).trim().equals("0")) {
                    columns.set(0, "88888888" + " ");
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
