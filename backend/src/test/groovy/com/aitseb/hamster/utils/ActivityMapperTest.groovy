package com.aitseb.hamster.utils

import com.aitseb.hamster.dto.ActivityDTO
import com.aitseb.hamster.dto.WeekSummary
import spock.lang.Specification
import spock.lang.Unroll

import static com.aitseb.hamster.dto.StravaActivityType.Hike
import static com.aitseb.hamster.dto.StravaActivityType.Ride
import static com.aitseb.hamster.dto.StravaActivityType.Run
import static com.aitseb.hamster.dto.StravaActivityType.Swim

class ActivityMapperTest extends Specification {

    @Unroll
    def 'should create week summary for activities: #activity1, #activity2, #activity3'() {
        expect:
        ActivityMapper.createWeekSummary([activity1, activity2, activity3], 1) == weekSummary

        where:
        activity1                                    | activity2                                            | activity3                                    || weekSummary
        activity(Run, 60, 30, 56, 0, 245, '12.3')    | activity(Ride, 135, 125, 110, 90.5, 1450, '92.3 km') | activity(Swim, 70, 25, 20, 0, 0, '1500m')    || weekSum(1, 4.42, 3, 186, 90.5, 1695, 106.1)
        activity(Ride, 60, 30, 40, 60, 900, '45km')  | activity(Ride, 120, 90, 50, 30.5, 150, '50')         | activity(Hike, 240, 30, 30, 0, 1000, '12.4') || weekSum(1, 7, 2.5, 120, 90.5, 2050, 107.4)
    }

    ActivityDTO activity(sport, time, rege, effort, tss, elevation, distance) {
        return ActivityDTO.builder()
                .type(sport)
                .time(time)
                .regeTime(rege)
                .effort(effort)
                .tss(tss)
                .elevation(elevation)
                .distance(distance)
                .build()
    }

    WeekSummary weekSum(weekOfYear, timeSum, regeSum, effortSum, tssSum, elevationSum, distanceSum) {
        new WeekSummary(weekOfYear, timeSum, regeSum, effortSum, tssSum, elevationSum, distanceSum)
    }
}
