package com.aitseb.hamster.utils;
import com.aitseb.hamster.dto.StravaActivityType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aitseb.hamster.dto.StravaActivityType.*;

public class TSVFileTransformer {

    public static void main(String[] args) {
        String hamsterDir = System.getProperty("user.dir");
        String inputFilePath = hamsterDir + "/backend/src/main/resources/" + "Rozpiska2024.tsv";
        String outputFilePath = hamsterDir + "/backend/src/main/resources/" + "Rozpiska2024.csv";

        try {
            convertTabsToPipes(inputFilePath, outputFilePath);
            System.out.println("Conversion completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error during conversion: " + e.getMessage());
        }
    }

    public static void convertTabsToPipes(String inputFilePath, String outputFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            String lastActivityDate = "";
            int sameDateCounter = 1;
            while ((line = reader.readLine()) != null) {
                List<String> columns = new ArrayList<>(Arrays.asList(line.split("\t")));
                columns.set(0, "0"); // strava_id
                if (lastActivityDate.equals(columns.get(2))) {
                    sameDateCounter++;
                } else {
                    sameDateCounter = 1;
                }
                lastActivityDate = columns.get(2);
                StravaActivityType activityType = mapActivityTypePolishNameToStravaActivityType(columns.get(4));
                if (!columns.get(2).startsWith("2024")) {
                    throw new IllegalArgumentException("Wrong year in activity. Should be 2024 and not: " + columns.get(2));
                }
                columns.set(1, columns.get(2) + "T0" + sameDateCounter + ":00:00");
                columns.set(2, activityType.name());
                columns.set(3, columns.get(16));
                columns.set(4, columns.get(5));
                columns.set(5, columns.get(6).isEmpty() ? "0" : columns.get(6));
                columns.set(6, columns.get(7).isEmpty() ? "0" : columns.get(7));
                columns.set(7, columns.get(8).isEmpty() ? "0" : columns.get(8));
                columns.set(8, columns.get(9).isEmpty() ? "0" : columns.get(9));
                columns.set(9, columns.get(10).isEmpty() ? "0" : columns.get(10));
                columns.set(10, columns.get(11).isEmpty() ? "0" : columns.get(11).replace(',', '.'));
                columns.set(11, columns.get(12).isEmpty() ? "0" : columns.get(12).replace(',', '.'));
                columns.set(12, "0"); // effort
                columns.set(13, columns.get(13).isEmpty() ? "0" : columns.get(13));
                columns.set(14, columns.get(14).isEmpty() ? "0" : Float.toString(parseSpeed(activityType, columns.get(14))));
                columns.set(15, Integer.toString(parseDistance(activityType, columns.get(15))));

                if (columns.size() == 17) {
                    columns.set(16, "");
                }
                if (columns.size() == 18) {
                    columns.set(16, columns.get(17));
                    columns.remove(17);
                }
                if (columns.size() == 19) {
                    columns.set(16, columns.get(17));
                    columns.remove(17);
                    columns.remove(17);
                }
                if (columns.size() == 20) {
                    String[] stravaUrl = columns.get(19).split("/");
                    columns.set(0, stravaUrl[stravaUrl.length-1]);
                    columns.set(16, columns.get(17));
                    columns.remove(17);
                    columns.remove(17);
                    columns.remove(17);
                }

                String convertedLine = String.join(" | ", columns);

                writer.write(convertedLine);
                writer.newLine();
            }
        }
    }

    private static StravaActivityType mapActivityTypePolishNameToStravaActivityType(String name) {
        switch (name.strip()) {
            case "Narty" -> { return AlpineSki; }
            case "Skitury" -> { return BackcountrySki; }
            case "Góry" -> { return Hike; }
            case "Rower" -> { return Ride; }
            case "Bieganie" -> { return Run; }
            case "Pływanie" -> { return Swim; }
            case "Siłownia" -> { return WeightTraining; }
            case "Workout", "Koszykówka" -> { return Workout; }
            default -> {
                System.err.println("Error during mapping polish activity type name to strava one: " + name);
                return null;
            }
        }
    }

    private static int parseDistance(StravaActivityType type, String distance) {
        if (type.equals(StravaActivityType.Swim)) {
            return Units.parseDistanceInMetres(distance);
        } else {
            return Units.parseDistanceInKmToMetres(distance);
        }
    }

    private static float parseSpeed(StravaActivityType type, String value) {
        if (value.isBlank()) {
            return 0;
        }
        switch (type) {
            case Run -> { return Units.runPaceToMs(value); }
            case Ride -> { return Units.kmhToMs(value); }
            case Swim -> { return Units.swimPaceToMs(value); }
        }
        throw new IllegalArgumentException("Speed shouldn't be set for " + type + ": " + value);
    }
}

