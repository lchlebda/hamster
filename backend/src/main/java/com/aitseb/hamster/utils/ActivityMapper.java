package com.aitseb.hamster.utils;

import com.aitseb.hamster.dao.Activity;
import com.aitseb.hamster.dto.ActivityDTO;
import com.aitseb.hamster.dto.DayOfWeekLowerCase;
import com.aitseb.hamster.dto.StravaActivity;
import com.aitseb.hamster.dto.WeekSummary;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;

import static com.aitseb.hamster.utils.Units.*;

public final class ActivityMapper {

    public static ActivityDTO fromDAOToDTO(Activity activity) {

        LocalDate date = activity.getDate().toLocalDate();

        return ActivityDTO.builder()
                .id(activity.getId())
                .stravaId(activity.getStravaId())
                .date(date)
                .weekOfYear(date.get(WeekFields.ISO.weekOfYear()))
                .dayOfWeek(DayOfWeekLowerCase.getFor(date.getDayOfWeek()))
                .type(activity.getSport())
                .title(activity.getDescription())
                .notes(activity.getNotes())
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
                .distance((int) stravaActivity.distance())
                .build();
    }

    public static WeekSummary createWeekSummary(List<ActivityDTO> activities, int week) {
        return new WeekSummary(week, 10, 2, 200, 300, 3400, 45);
    }

    private static String mapDistance(Activity activity) {
        return switch (activity.getSport()) {
            case WeightTraining, Workout -> "";
            case Swim -> activity.getDistance() + " m";
            default -> Math.round(activity.getDistance()/1000f * 10)/10.f + " km";
        };
    }

    private static String mapSpeed(Activity activity) {
        return switch (activity.getSport()) {
            case Ride -> msToKmh(activity.getSpeed()) + " km/h";
            case Run -> msToRunPace(activity.getSpeed()) + "/km";
            case Swim -> msToSwimPace(activity.getSpeed()) + "/100m";
            default -> "";
        };
    }
}
