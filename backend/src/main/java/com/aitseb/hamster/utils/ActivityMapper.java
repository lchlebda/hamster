package com.aitseb.hamster.utils;

import com.aitseb.hamster.dao.Activity;
import com.aitseb.hamster.dto.ActivityDTO;
import com.aitseb.hamster.dto.StravaActivity;

import static com.aitseb.hamster.utils.Units.*;

public final class ActivityMapper {

    public static ActivityDTO fromDAOToDTO(Activity activity) {
        return ActivityDTO.builder()
                .stravaId(activity.getStravaId())
                .date(activity.getDate().toLocalDate())
                .type(activity.getSport())
                .title(activity.getDescription())
                .time(activity.getTime())
                .hr(Math.round(activity.getHr()))
                .hrMax(Math.round(activity.getHrMax()))
                .cadence(Math.round(activity.getCadence()))
                .power(activity.getPower())
                .effort(activity.getEffort())
                .tss(activity.getTss())
                .elevation(Math.round(activity.getElevation()))
                .speed(mapSpeed(activity))
                .distance(mapDistance(activity))
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

    private static String mapDistance(Activity activity) {
        return switch (activity.getSport()) {
            case WeightTraining, Workout -> "";
            case Swim -> (int)activity.getDistance() + " m";
            default -> Math.round(activity.getDistance()/1000*10)/10.f + " km";
        };
    }

    private static String mapSpeed(Activity activity) {
        return switch (activity.getSport()) {
            case Ride -> msToKmh(activity.getSpeed()) + " km/h";
            case Run -> msToPace(activity.getSpeed()) + "/km";
            default -> "";
        };
    }
}
