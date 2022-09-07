package com.aitseb.hamster.utils;

import java.util.concurrent.TimeUnit;

public final class Units {

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
    public static String msToPace(float ms) {
        int seconds = (int) Math.floor(1000 / ms);
        int minutesPace = seconds / 60;
        int secondsPace = seconds % 60;

        return minutesPace + ":" + (secondsPace < 10 ? "0" : "") + secondsPace;
    }
}
