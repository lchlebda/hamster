package com.aitseb.hamster.repository;

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

    private static final String url = "https://www.strava.com/api/v3/athlete/activities";
    private final RestTemplate restTemplate;

    public List<StravaActivity> getList(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<StravaActivity[]> list = null;
        try {
            list = restTemplate.exchange(url, HttpMethod.GET, requestEntity, StravaActivity[].class);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return asList(list.getBody());
    }

}
