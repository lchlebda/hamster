package com.aitseb.hamster.utils;

import com.aitseb.hamster.dto.Activity;
import com.aitseb.hamster.dto.StravaActivity;

import static com.aitseb.hamster.utils.Units.*;

public final class ActivityMapper {

    public static Activity fromStrava(StravaActivity stravaActivity) {
        return Activity.builder()
                .id(stravaActivity.id())
                .date(stravaActivity.start_date())
                .type(stravaActivity.type())
                .title(stravaActivity.name())
                .time(secondsToMinutesByFive(stravaActivity.moving_time()))
                .hr(Math.round(stravaActivity.average_heartrate()))
                .hrMax(Math.round(stravaActivity.max_heartrate()))
                .cadence(Math.round(stravaActivity.average_cadence()))
                .power(stravaActivity.weighted_average_watts())
                .effort(stravaActivity.suffer_score())
                .elevation(Math.round(stravaActivity.total_elevation_gain()))
                .speed(mapSpeed(stravaActivity))
                .distance(mapDistance(stravaActivity))
                .build();
    }

    private static String mapDistance(StravaActivity stravaActivity) {
        return switch (stravaActivity.type()) {
            case WeightTraining, Workout -> "";
            case Swim -> (int)stravaActivity.distance() + " m";
            default -> Math.round(stravaActivity.distance()/1000*10)/10.f + " km";
        };
    }

    private static String mapSpeed(StravaActivity stravaActivity) {
        return switch (stravaActivity.type()) {
            case Ride -> msToKmh(stravaActivity.average_speed()) + " km/h";
            case Run -> msToPace(stravaActivity.average_speed()) + "/km";
            default -> "";
        };
    }
}
