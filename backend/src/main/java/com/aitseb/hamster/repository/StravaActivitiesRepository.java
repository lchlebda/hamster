package com.aitseb.hamster.repository;

import com.aitseb.hamster.dto.StravaActivityStream;
import com.aitseb.hamster.dto.StravaActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Arrays.asList;

@Repository
@RequiredArgsConstructor
public class StravaActivitiesRepository {

    private static final String ATHLETE_ACTIVITIES_URL = "https://www.strava.com/api/v3/athlete/activities?after={after}";
    private static final String HEARTRATE_STREAM_URL = "http://www.strava.com/api/v3/activities/7817317716/streams?keys=heartrate";
    private final RestTemplate restTemplate;

    public List<StravaActivity> getList(String accessToken, long after) {
        HttpEntity<Object> requestEntity = getRequestEntityWithHeaders(accessToken);
        ResponseEntity<StravaActivity[]> list = null;
        try {
            list = restTemplate.exchange(
                    ATHLETE_ACTIVITIES_URL,
                    HttpMethod.GET,
                    requestEntity,
                    StravaActivity[].class,
                    after);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return asList(list.getBody());
    }

    public StravaActivityStream[] getActivitiesStreamWithHeartrate(String accessToken) {
        HttpEntity<Object> requestEntity = getRequestEntityWithHeaders(accessToken);
        ResponseEntity<StravaActivityStream[]> list = null;
        try {
            list = restTemplate.exchange(
                    HEARTRATE_STREAM_URL,
                    HttpMethod.GET,
                    requestEntity,
                    StravaActivityStream[].class);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return list.getBody();
    }

    private HttpEntity<Object> getRequestEntityWithHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        return new HttpEntity<>(headers);
    }

}
