package com.aitseb.hamster.dto;

import java.time.LocalDateTime;

public record StravaActivity(long id,
                             LocalDateTime start_date,
                             StravaActivityType type,
                             int moving_time,
                             float average_heartrate,
                             float max_heartrate,
                             float average_cadence,
                             int weighted_average_watts,
                             float suffer_score,
                             float total_elevation_gain,
                             String name,
                             float average_speed,
                             float distance) {
}
