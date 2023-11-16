package com.aitseb.hamster.dto;

import java.time.DayOfWeek;

public enum DayOfWeekLowerCase {
    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;

    public static DayOfWeekLowerCase getFor(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY -> { return Monday; }
            case TUESDAY -> { return Tuesday; }
            case WEDNESDAY -> { return Wednesday; }
            case THURSDAY -> { return Thursday; }
            case FRIDAY -> { return Friday; }
            case SATURDAY -> { return Saturday; }
            case SUNDAY -> { return Sunday; }
        }

        return null;
    }
}
