package com.aitseb.hamster.utils

import spock.lang.Specification
import spock.lang.Unroll

import java.time.DayOfWeek

class DateHelperTest extends Specification {

    @Unroll
    def 'should starts week view year after Sunday for #year'() {
        expect:
        DateHelper.weekViewYearStartsAfter(year).getDayOfWeek() == DayOfWeek.SUNDAY

        where:
        year << [2023, 2022, 2021, 2020, 2019, 2018, 2015]
    }

    @Unroll
    def 'should ends week view year before Monday for #year'() {
        expect:
        DateHelper.weekViewYearEndsBefore(year).getDayOfWeek() == DayOfWeek.MONDAY

        where:
        year << [2023, 2022, 2021, 2020, 2019, 2018, 2015]
    }
}
