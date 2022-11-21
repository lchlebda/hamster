package com.aitseb.hamster.controller

import com.aitseb.hamster.dao.Activity
import com.aitseb.hamster.dto.StravaActivity
import com.aitseb.hamster.exception.StravaException
import com.aitseb.hamster.repository.ActivitiesRepository
import com.aitseb.hamster.repository.StravaActivitiesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Unroll

import java.sql.Timestamp

import static com.aitseb.hamster.dto.StravaActivityType.*
import static java.time.LocalDateTime.of
import static java.time.Month.JANUARY
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ActivitiesRepository.class)
class ActivitiesControllerTest extends Specification {

    def ACCESS_TOKEN = 'access_token'

    @Autowired private MockMvc mvc

    def stravaActivitiesRepository = Stub(StravaActivitiesRepository)
    def activitiesRepository = Mock(ActivitiesRepository)

    def setup() {
        mvc = MockMvcBuilders.standaloneSetup(
                new ActivitiesController(stravaActivitiesRepository, activitiesRepository))
                .setControllerAdvice(new ExceptionHandlerController())
                .build()
    }

    def 'should return sorted activities with latest Strava activities when GET /activities'() {
        given:
        def activity1 = activity(123, of(2022, JANUARY, 10, 12, 20), Run)
        def activity2 = activity(1234, of(2022, JANUARY, 12, 12, 20), Ride)
        def stravaActivity2 = stravaActivity(1234, of(2022, JANUARY, 12, 12, 20), Ride)
        def activity3 = activity(12345, of(2022, JANUARY, 20, 12, 20), Swim)
        def stravaActivity3 = stravaActivity(12345, of(2022, JANUARY, 20, 12, 20), Swim)
        def activity4 = activity(123456, of(2022, JANUARY, 21, 12, 20), Workout)
        def stravaActivity4 = stravaActivity(123456, of(2022, JANUARY, 21, 12, 20), Workout)
        def timestamp = Timestamp.valueOf(activity2.getDate()).getTime()/1000

        activitiesRepository.findFirstByOrderByDateDesc() >> activity2
        //noinspection GroovyAssignabilityCheck
        stravaActivitiesRepository.getList(ACCESS_TOKEN, timestamp) >> [stravaActivity2, stravaActivity3, stravaActivity4]
        activitiesRepository.findAll() >> [activity1, activity2, activity3, activity4]

        when:
        def result = mvc.perform(get('/activities').header('ACCESS_TOKEN', ACCESS_TOKEN))

        then:
        result.andExpect(jsonPath('$.[0].stravaId', is(123456)))
              .andExpect(jsonPath('$.[0].date[0]', is(2022)))
              .andExpect(jsonPath('$.[0].date[1]', is(1)))
              .andExpect(jsonPath('$.[0].date[2]', is(21)))
              .andExpect(jsonPath('$.[0].type', is(Workout.toString())))
              .andExpect(jsonPath('$', hasSize(4)))
              .andExpect(status().is(HttpStatus.OK.value()))

        0 * activitiesRepository.save(activity2)
        1 * activitiesRepository.save(activity3)
        1 * activitiesRepository.save(activity4)
    }

    def 'should return 206 (partial content) when Strava service is failing'() {
        given:
        def activity1 = activity(123, of(2022, JANUARY, 10, 12, 20), Run)
        def timestamp = Timestamp.valueOf(activity1.getDate()).getTime()/1000

        activitiesRepository.findFirstByOrderByDateDesc() >> activity1
        //noinspection GroovyAssignabilityCheck
        stravaActivitiesRepository.getList(ACCESS_TOKEN, timestamp) >> { throw new StravaException('') }
        activitiesRepository.findAll() >> [activity1]

        when:
        def result = mvc.perform(get('/activities').header('ACCESS_TOKEN', ACCESS_TOKEN))

        then:
        result.andExpect(jsonPath('$.[0].stravaId', is(123)))
                .andExpect(jsonPath('$.[0].date[0]', is(2022)))
                .andExpect(jsonPath('$.[0].date[1]', is(1)))
                .andExpect(jsonPath('$.[0].date[2]', is(10)))
                .andExpect(jsonPath('$.[0].type', is(Run.toString())))
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(status().is(HttpStatus.PARTIAL_CONTENT.value()))

        0 * activitiesRepository.save()
    }

    @Unroll
    def 'should throw IllegalArgumentException when speed is set for #type'() {
        given:
        def id = 111
        def value = 55

        when:
        def result = mvc.perform(post("/activities/update/$id?type=$type&prop=speed&value=$value"))

        then:
        result.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        0 * activitiesRepository.updateSpeed(id, value)

        where:
        type << [AlpineSki, BackcountrySki, Hike, Walk, WeightTraining, Workout]
    }

    @Unroll
    def 'should throw IllegalArgumentException when distance is set for #type'() {
        given:
        def id = 111
        def value = 55

        when:
        def result = mvc.perform(post("/activities/update/$id?type=$type&prop=distance&value=$value"))

        then:
        result.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        0 * activitiesRepository.updateDistance(id, value)

        where:
        type << [WeightTraining, Workout]
    }

    Activity activity(stravaId, date, sport) {
        def activity = new Activity()
        activity.setStravaId(stravaId)
        activity.setDate(date)
        activity.setSport(sport)
        activity
    }

    StravaActivity stravaActivity(id, start_date, type) {
        new StravaActivity(id, start_date, type, 0, 0, 0, 0, 0, 0, 0, '', 0, 0)
    }
}
