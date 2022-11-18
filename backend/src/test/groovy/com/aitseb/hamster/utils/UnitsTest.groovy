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
        ms     || kmh
        5f     || 18f
        4.5f   || 16.2f
        9f     || 32.4f
        7.178f || 25.8f
        6.795f || 24.5f
        8.888f || 32f
    }

    @Unroll
    def 'should calculate #kmh km/h to #ms m/s'() {
        expect:
        Units.kmhToMs(kmh) == ms

        where:
        kmh         || ms
        '18 km/h'   || 5f
        '18'   || 5f
        '16.2km/h'  || 4.5f
        '32.4'      || 9f
        '25.8 '     || 7.166f
        '24.5 km/h' || 6.805f
        '32km/h'    || 8.888f
        ''          || 0f
        '   '       || 0f
    }

    @Unroll
    def 'should throw NumberFormatException when calculate `#kmh` to m/s'() {
        when:
        Units.kmhToMs(kmh)

        then:
        thrown(NumberFormatException)

        where:
        kmh << ['k18', 'km/h', 'aaa', 'C20.5 km/h', '24 km/h 23', '32 22', '23.4 4', '32,4 km/h']
    }

    @Unroll
    def 'should calculate #ms m/s to run pace #pace/km'() {
        expect:
        Units.msToRunPace(ms) == pace

        where:
        ms     || pace
        5f     || '3:20'
        4f     || '4:10'
        3.333f || '5:00'
        3.344f || '4:59'
        3.322f || '5:01'
        3.184f || '5:14'
    }

    @Unroll
    def 'should calculate run pace #pace/km to #ms m/s'() {
        expect:
        Units.runPaceToMs(pace) == ms

        where:
        pace        || ms
        '3:20/km'   || 5f
        '4:10 /km'  || 4f
        '5:00 /km ' || 3.333f
        '4:59'      || 3.344f
        '5:01'      || 3.322f
        '5:14'      || 3.184f
    }

    @Unroll
    def 'should throw NumberFormatException when calculate run pace `#pace` to m/s'() {
        when:
        Units.runPaceToMs(pace)

        then:
        thrown(NumberFormatException)

        where:
        pace << ['k4:20', ' /km', 'aaa', 'd 5:10 /km', '5', '4:60', '3:001', '5:000 /km', '4,54 ']
    }

    @Unroll
    def 'should calculate #ms m/s to swim pace #pace/100m'() {
        expect:
        Units.msToSwimPace(ms) == pace

        where:
        ms     || pace
        0.518f || '3:13'
        0.833f || '2:00'
        0.834f || '1:59'
        0.827f || '2:00'
        0.826f || '2:01'
    }

    @Unroll
    def 'should calculate swim pace #pace/100m to #ms m/s'() {
        expect:
        Units.swimPaceToMs(pace) == ms

        where:
        pace          || ms
        '3:13/100m'   || 0.518f
        '2:00 /100m'  || 0.833f
        '1:59 /100m ' || 0.84f
        '2:00 '       || 0.833f
        '2:01'        || 0.826f
    }

    @Unroll
    def 'should throw NumberFormatException when calculate swim pace `#pace` to m/s'() {
        when:
        Units.swimPaceToMs(pace)

        then:
        thrown(NumberFormatException)

        where:
        pace << ['k4:20', ' /100m', 'aaa', 'd 5:10 /100m', '2', '3:60', '2:001', '3:000 /km', '1,54 ']
    }

}

