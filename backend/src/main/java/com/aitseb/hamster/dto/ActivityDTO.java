package com.aitseb.hamster.dto;

import lombok.Builder;

import java.time.LocalDate;

public record ActivityDTO(long id,
                          long stravaId,
                          LocalDate date,
                          DayOfWeekLowerCase dayOfWeek,
                          StravaActivityType type,
                          String title,
                          int time,
                          int regeTime,
                          int hr,
                          int hrMax,
                          int cadence,
                          int power,
                          float ef,
                          float tss,
                          int effort,
                          int elevation,
                          String speed,
                          String distance,
                          String notes) {
    @Builder public ActivityDTO {} // this default constructor is workaround for bug: https://youtrack.jetbrains.com/issue/IDEA-266513/Problem-with-lombok-Builder-and-record
}
