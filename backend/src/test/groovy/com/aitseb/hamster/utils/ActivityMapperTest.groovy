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
        ActivityMapper.createWeekSummary([activity1, activity2, activity3]) == weekSummary

        where:
        activity1                                              | activity2                                                      | activity3                                              || weekSummary
        activity('2023_1', Run, 60, 30, 56, 0, 245, '12.3')    | activity('2023_1', Ride, 135, 125, 110, 90.5, 1450, '92.3 km') | activity('2023_1', Swim, 70, 25, 20, 0, 0, '1500m')    || weekSum(1, 2023, 4.42, 3, 186, 90.5, 1695, 106.1)
        activity('2023_1', Ride, 60, 30, 40, 60, 900, '45km')  | activity('2023_1', Ride, 120, 90, 50, 30.5, 150, '50')         | activity('2023_1', Hike, 240, 30, 30, 0, 1000, '12.4') || weekSum(1, 2023, 7, 2.5, 120, 90.5, 2050, 107.4)
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

    @Unroll
    def 'should calculate yearWeekKey for the end/begin of the year: when first day of year is #firstDayOfYear and the date is #dayOfWeek then yearWeekKey is #yearWeekKey'() {
        expect:
        ActivityMapper.fromDAOToDTO(activity(date.atStartOfDay(), Run))
                .yearWeekKey() == yearWeekKey

        where:
        firstDayOfYear | date                       | dayOfWeek   || yearWeekKey
        'Monday'       | LocalDate.of(2024, 1, 1)   | 'Monday'    || '2024_1'
        'Monday'       | LocalDate.of(2024, 1, 2)   | 'Tuesday'   || '2024_1'
        'Monday'       | LocalDate.of(2024, 1, 3)   | 'Wednesday' || '2024_1'
        'Monday'       | LocalDate.of(2024, 1, 4)   | 'Thursday'  || '2024_1'
        'Monday'       | LocalDate.of(2024, 1, 5)   | 'Friday'    || '2024_1'
        'Monday'       | LocalDate.of(2024, 1, 6)   | 'Saturday'  || '2024_1'
        'Monday'       | LocalDate.of(2024, 1, 7)   | 'Sunday'    || '2024_1'
        'Tuesday'      | LocalDate.of(2018, 12, 31) | 'Monday'    || '2019_1'
        'Tuesday'      | LocalDate.of(2019, 1, 1)   | 'Tuesday'   || '2019_1'
        'Tuesday'      | LocalDate.of(2019, 1, 2)   | 'Wednesday' || '2019_1'
        'Tuesday'      | LocalDate.of(2019, 1, 3)   | 'Thursday'  || '2019_1'
        'Tuesday'      | LocalDate.of(2019, 1, 4)   | 'Friday'    || '2019_1'
        'Tuesday'      | LocalDate.of(2019, 1, 5)   | 'Saturday'  || '2019_1'
        'Tuesday'      | LocalDate.of(2019, 1, 6)   | 'Sunday'    || '2019_1'
        'Wednesday'    | LocalDate.of(2019, 12, 30) | 'Monday'    || '2020_1'
        'Wednesday'    | LocalDate.of(2019, 12, 31) | 'Tuesday'   || '2020_1'
        'Wednesday'    | LocalDate.of(2020, 1, 1)   | 'Wednesday' || '2020_1'
        'Wednesday'    | LocalDate.of(2020, 1, 2)   | 'Thursday'  || '2020_1'
        'Wednesday'    | LocalDate.of(2020, 1, 3)   | 'Friday'    || '2020_1'
        'Wednesday'    | LocalDate.of(2020, 1, 4)   | 'Saturday'  || '2020_1'
        'Wednesday'    | LocalDate.of(2020, 1, 5)   | 'Sunday'    || '2020_1'
        'Thursday'     | LocalDate.of(2014, 12, 29) | 'Monday'    || '2015_1'
        'Thursday'     | LocalDate.of(2014, 12, 30) | 'Tuesday'   || '2015_1'
        'Thursday'     | LocalDate.of(2014, 12, 31) | 'Wednesday' || '2015_1'
        'Thursday'     | LocalDate.of(2015, 1, 1)   | 'Thursday'  || '2015_1'
        'Thursday'     | LocalDate.of(2015, 1, 2)   | 'Friday'    || '2015_1'
        'Thursday'     | LocalDate.of(2015, 1, 3)   | 'Saturday'  || '2015_1'
        'Thursday'     | LocalDate.of(2015, 1, 4)   | 'Sunday'    || '2015_1'
        'Friday'       | LocalDate.of(2020, 12, 28) | 'Monday'    || '2020_53'
        'Friday'       | LocalDate.of(2020, 12, 29) | 'Tuesday'   || '2020_53'
        'Friday'       | LocalDate.of(2020, 12, 30) | 'Wednesday' || '2020_53'
        'Friday'       | LocalDate.of(2020, 12, 31) | 'Thursday'  || '2020_53'
        'Friday'       | LocalDate.of(2021, 1, 1)   | 'Friday'    || '2020_53'
        'Friday'       | LocalDate.of(2021, 1, 2)   | 'Saturday'  || '2020_53'
        'Friday'       | LocalDate.of(2021, 1, 3)   | 'Sunday'    || '2020_53'
        'Saturday'     | LocalDate.of(2021, 12, 27) | 'Monday'    || '2021_52'
        'Saturday'     | LocalDate.of(2021, 12, 28) | 'Tuesday'   || '2021_52'
        'Saturday'     | LocalDate.of(2021, 12, 29) | 'Wednesday' || '2021_52'
        'Saturday'     | LocalDate.of(2021, 12, 30) | 'Thursday'  || '2021_52'
        'Saturday'     | LocalDate.of(2021, 12, 31) | 'Friday'    || '2021_52'
        'Saturday'     | LocalDate.of(2022, 1, 1)   | 'Saturday'  || '2021_52'
        'Saturday'     | LocalDate.of(2022, 1, 2)   | 'Sunday'    || '2021_52'
        'Sunday'       | LocalDate.of(2022, 12, 26) | 'Monday'    || '2022_52'
        'Sunday'       | LocalDate.of(2022, 12, 27) | 'Tuesday'   || '2022_52'
        'Sunday'       | LocalDate.of(2022, 12, 28) | 'Wednesday' || '2022_52'
        'Sunday'       | LocalDate.of(2022, 12, 29) | 'Thursday'  || '2022_52'
        'Sunday'       | LocalDate.of(2022, 12, 30) | 'Friday'    || '2022_52'
        'Sunday'       | LocalDate.of(2022, 12, 31) | 'Saturday'  || '2022_52'
        'Sunday'       | LocalDate.of(2023, 1, 1)   | 'Sunday'    || '2022_52'
    }

    ActivityDTO activity(yearWeekKey, sport, time, rege, effort, tss, elevation, distance) {
        return ActivityDTO.builder()
                .yearWeekKey(yearWeekKey)
                .type(sport)
                .time(time)
                .regeTime(rege)
                .effort(effort)
                .tss(tss)
                .elevation(elevation)
                .distance(distance)
                .build()
    }

    WeekSummary weekSum(weekOfYear, year, timeSum, regeSum, effortSum, tssSum, elevationSum, distanceSum) {
        new WeekSummary(weekOfYear, year, timeSum, regeSum, effortSum, tssSum, elevationSum, distanceSum)
    }

    Activity activity(date, sport) {
        def activity = new Activity()
        activity.setDate(date)
        activity.setSport(sport)
        activity
    }
}
