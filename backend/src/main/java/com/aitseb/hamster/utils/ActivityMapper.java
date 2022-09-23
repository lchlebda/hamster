package com.aitseb.hamster.utils;

import com.aitseb.hamster.dao.Activity;
import com.aitseb.hamster.dto.ActivityDTO;
import com.aitseb.hamster.dto.StravaActivity;

import static com.aitseb.hamster.utils.Units.*;

public final class ActivityMapper {

    public static ActivityDTO fromStravaToDTO(StravaActivity stravaActivity) {
        return ActivityDTO.builder()
                .stravaId(stravaActivity.id())
                .date(stravaActivity.start_date())
                .type(stravaActivity.type())
                .title(stravaActivity.name())
                .time(secondsToMinutesByFive(stravaActivity.moving_time()))
                .hr(Math.round(stravaActivity.average_heartrate()))
                .hrMax(Math.round(stravaActivity.max_heartrate()))
                .cadence(Math.round(stravaActivity.average_cadence()))
                .power(stravaActivity.weighted_average_watts())
                .effort((int)stravaActivity.suffer_score())
                .elevation(Math.round(stravaActivity.total_elevation_gain()))
                .speed(mapSpeed(stravaActivity))
                .distance(mapDistance(stravaActivity))
                .build();
    }

    public static Activity fromStravaToDAO(StravaActivity stravaActivity) {
        return Activity.builder()
                .stravaId(stravaActivity.id())
                .date(stravaActivity.start_date())
                .sport(stravaActivity.type())
                .time(secondsToMinutesByFive(stravaActivity.moving_time()))
                .hr(Math.round(stravaActivity.average_heartrate()))
                .hrMax(Math.round(stravaActivity.max_heartrate()))
                .cadence(Math.round(stravaActivity.average_cadence()))
                .power(stravaActivity.weighted_average_watts())
                .effort((int)stravaActivity.suffer_score())
                .elevation(Math.round(stravaActivity.total_elevation_gain()))
                .speed(stravaActivity.average_speed())
                .distance(stravaActivity.distance())
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
