package com.aitseb.hamster.dto;

import lombok.Builder;

import java.time.LocalDate;

public record Activity(long id,
                       LocalDate date,
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
                       float effort,
                       float elevation,
                       float speed,
                       float distance,
                       String notes) {
    @Builder public Activity {} // this default constructor is workaround for bug: https://youtrack.jetbrains.com/issue/IDEA-266513/Problem-with-lombok-Builder-and-record
}
