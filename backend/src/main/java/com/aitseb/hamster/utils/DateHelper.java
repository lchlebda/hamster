package com.aitseb.hamster.utils;

import java.time.LocalDate;

public class DateHelper {

    public static LocalDate weekViewYearStartsAfter(int year) {
        LocalDate firstDayOfYear = LocalDate.ofYearDay(year, 1);
        int dayOfWeek = firstDayOfYear.getDayOfWeek().getValue();

        return firstDayOfYear.minusDays(dayOfWeek);
    }

    public static LocalDate weekViewYearEndsBefore(int year) {
        LocalDate lastDayOfYear = LocalDate.ofYearDay(year+1, 1).minusDays(1);
        int dayOfWeek = lastDayOfYear.getDayOfWeek().ordinal();

        return lastDayOfYear.plusDays(7-dayOfWeek);
    }

}
