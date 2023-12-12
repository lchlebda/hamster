package com.aitseb.hamster.dto;

public record WeekSummary(int weekOfYear,
                          int year,
                          float activityHours,
                          float regeHours,
                          int effort,
                          float tss,
                          int elevation,
                          float distance) { }
