package com.aitseb.hamster.utils

import com.aitseb.hamster.dao.Activity
import com.aitseb.hamster.dto.ActivityDTO
import com.aitseb.hamster.dto.WeekSummary
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static com.aitseb.hamster.dto.StravaActivityType.*

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

    @Unroll
    def 'should calculate week of year for every day in that week: when first day of year is #firstDayOfYear then week of year is #weekOfYear'() {
        expect:
        ActivityMapper.fromDAOToDTO(activity(date.atStartOfDay(), Run))
                .weekOfYear() == weekOfYear

        where:
        firstDayOfYear | date                     || weekOfYear
        'Monday'       | LocalDate.of(2024, 1, 1) || 1
        'Tuesday'      | LocalDate.of(2019, 1, 1) || 1
        'Wednesday'    | LocalDate.of(2020, 1, 1) || 1
        'Thursday'     | LocalDate.of(2015, 1, 1) || 1
        'Friday'       | LocalDate.of(2021, 1, 1) || 53
        'Saturday'     | LocalDate.of(2022, 1, 1) || 52
        'Sunday'       | LocalDate.of(2023, 1, 1) || 52
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

    Activity activity(date, sport) {
        def activity = new Activity()
        activity.setDate(date)
        activity.setSport(sport)
        activity
    }
}
