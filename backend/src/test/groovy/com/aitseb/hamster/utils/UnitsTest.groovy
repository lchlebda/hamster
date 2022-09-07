package com.aitseb.hamster.utils

import spock.lang.Specification
import spock.lang.Unroll

class UnitsTest extends Specification {

    @Unroll
    def 'should rounds #seconds seconds to multiple of 5 in minutes: #minutes'() {
        expect:
        Units.secondsToMinutesByFive(seconds) == minutes

        where:
        seconds || minutes
        3600    || 60
        3659    || 60
        3599    || 60
        3360    || 60
        3660    || 65
        3800    || 65
    }

    @Unroll
    def 'should calculate #ms m/s to #kmh km/h'() {
        expect:
        Units.msToKmh(ms) == kmh

        where:
        ms   || kmh
        5f   || 18.0f
        4.5f || 16.2f
        9f   || 32.4f
    }

    @Unroll
    def 'should calculate #ms m/s to pace #pace/km'() {
        expect:
        Units.msToPace(ms) == pace

        where:
        ms    || pace
        5f    || '3:20'
        4f    || '4:10'
        3.33f || '5:00'
        3.34f || '4:59'
        3.32f || '5:01'
    }

}

