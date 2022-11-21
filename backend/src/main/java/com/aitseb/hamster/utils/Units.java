package com.aitseb.hamster.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Units {

    private final static int RUN_FACTOR = 1000;
    private final static int SWIM_FACTOR = 100;

    /**
     * Rounds to first multiple of 5 higher than duration (for durations with units more than one - i.e. 51, 66'30)
     * Rounds down to first multiple of 5 (for durations with units less than one - i.e. 50'55, 65'15)
     *
     * @param seconds duration time
     * @return duration time in minutes
     */
    public static int secondsToMinutesByFive(int seconds) {
        int minutes = (int) TimeUnit.SECONDS.toMinutes(seconds);
        if (Math.floorMod(minutes, 5) == 0) {
            return minutes;
        } else {
            return ((minutes / 5) + 1) * 5;
        }
    }

    /**
     * Converts m/s to km/h
     *
     * @param ms speed in m/s
     * @return speed in km/h
     */
    public static float msToKmh(float ms) {
        float kmh = ms * 3600/1000;
        return Math.round(kmh * 10) / 10.f;
    }

    /**
     * Converts m/s to pace in running
     *
     * @param ms speed in m/s
     * @return pace of running
     */
    public static String msToRunPace(float ms) {
        int seconds = (int) Math.floor(1000 / ms);
        int minutesPace = seconds / 60;
        int secondsPace = seconds % 60;

        return minutesPace + ":" + (secondsPace < 10 ? "0" : "") + secondsPace;
    }

    /**
     * Converts run pace to m/s
     *
     * @param pace speed in min:sec /km (may contain '/km' string at the end)
     * @return speed of running in m/s
     */
    public static float runPaceToMs(String pace) {
        if (pace.isBlank()) {
            return 0;
        }
        if (!Pattern.compile("^[1-7]:[0-5][0-9]\\s*(/km)?\\s*$").matcher(pace).matches()) {
            throw new NumberFormatException(pace + " has wrong run pace format.");
        }

        return parsePaceToMs(pace, RUN_FACTOR);
    }

    /**
     * Converts km/h to m/s
     *
     * @param kmh speed in km/h (may contain 'km/h' string at the end)
     * @return speed in m/s
     */
    public static float kmhToMs(String kmh) {
        if (kmh.isBlank()) {
            return 0;
        }
        if (!Pattern.compile("^\\d+(\\.\\d+)?\\s*(km/h)?\\s*$").matcher(kmh).matches()) {
            throw new NumberFormatException(kmh + " has wrong run pace format.");
        }
        Pattern pattern = Pattern.compile("^\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(kmh);
        if (matcher.find()) {
            float speed = Float.parseFloat(matcher.group(0));
            float ms = speed*1000/3600;

            return new BigDecimal(ms)
                    .setScale(3, RoundingMode.FLOOR)
                    .floatValue();
        }
        throw new NumberFormatException(kmh + " has wrong speed format.");
    }

    /**
     * Converts m/s to pace in swimming
     *
     * @param ms speed in m/s
     * @return pace of swimming
     */
    public static String msToSwimPace(float ms) {
        int seconds = (int) Math.floor(1000 / ms / 10);
        int minutesPace = seconds / 60;
        int secondsPace = seconds % 60;

        return minutesPace + ":" + (secondsPace < 10 ? "0" : "") + secondsPace;
    }

    /**
     * Converts swim pace to m/s
     *
     * @param pace speed in min:sec /100m (may contain '/100m' string at the end)
     * @return speed of swimmin in m/s
     */

    public static float swimPaceToMs(String pace) {
        if (pace.isBlank()) {
            return 0;
        }
        if (!Pattern.compile("^[1-7]:[0-5][0-9]\\s*(/100m)?\\s*$").matcher(pace).matches()) {
            throw new NumberFormatException(pace + " has wrong swim pace format.");
        }

        return parsePaceToMs(pace, SWIM_FACTOR);
    }

    /**
     * Convert distance in km to meters
     *
     * @param distance in km (may contain 'km' string at the end)
     * @return distance in metres
     */
    public static int parseDistanceInKmToMetres(String distance) {
        if (distance.isBlank()) {
            return 0;
        }
        if (!Pattern.compile("^\\d+(\\.\\d+)?\\s*(km)?\\s*$").matcher(distance).matches()) {
            throw new NumberFormatException(distance + " has wrong distance format.");
        }
        Pattern pattern = Pattern.compile("^\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(distance);
        if (matcher.find()) {
            float distanceKm = Float.parseFloat(matcher.group(0));

            return (int) (distanceKm * 1000);
        }
        throw new NumberFormatException(distance + " has wrong distance format.");
    }

    /**
     * Convert distance in meters from String to int
     *
     * @param distance in m (may contain 'm' character at the end)
     * @return distance in metres as int
     */
    public static int parseDistanceInMetres(String distance) {
        if (distance.isBlank()) {
            return 0;
        }
        if (!Pattern.compile("^\\d+\\s*(m)?\\s*$").matcher(distance).matches()) {
            throw new NumberFormatException(distance + " has wrong distance format.");
        }
        Pattern pattern = Pattern.compile("^\\d+");
        Matcher matcher = pattern.matcher(distance);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(0));
        }
        throw new NumberFormatException(distance + " has wrong distance format.");
    }

    private static float parsePaceToMs(String pace, int factor) {
        Pattern pattern = Pattern.compile("^[1-7]:[0-5][0-9]");
        Matcher matcher = pattern.matcher(pace);
        if (matcher.find()) {
            String str = matcher.group(0);
            String[] arr = str.split(":");
            if (arr.length < 2) {
                throw new NumberFormatException(pace + " has wrong pace format.");
            }
            int minutes = Integer.parseInt(arr[0]);
            int seconds = Integer.parseInt(arr[1]);
            float ms = (float) factor/(minutes * 60 + seconds);

            return new BigDecimal(ms)
                    .setScale(3, RoundingMode.FLOOR)
                    .floatValue();
        }
        throw new NumberFormatException(pace + " has wrong pace format.");
    }
}
