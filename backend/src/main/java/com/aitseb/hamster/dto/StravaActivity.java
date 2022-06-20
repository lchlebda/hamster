package com.aitseb.hamster.dto;

public record StravaActivity(long id,
                             StravaActivityType type,
                             int moving_time,
                             String name,
                             float distance) {
}
