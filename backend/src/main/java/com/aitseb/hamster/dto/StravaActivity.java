package com.aitseb.hamster.dto;

import java.time.LocalDate;

public record StravaActivity(long id,
                             LocalDate start_date,
                             StravaActivityType type,
                             int moving_time,
                             float average_cadence,
                             int weighted_average_watts,
                             float suffer_score,
                             float total_elevation_gain,
                             String name,
                             float average_speed,
                             float distance) {
}
