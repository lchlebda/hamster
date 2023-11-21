package com.aitseb.hamster.dto;

public record WeekSummary(int weekOfYear,
                          float activityHours,
                          float regeHours,
                          float ef,
                          float tss,
                          int elevation,
                          float distance) { }
