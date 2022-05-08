package com.aitseb.hamster.service;

import com.aitseb.hamster.dto.StravaToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class StravaAuthorizationService {

    private static final String url = "https://www.strava.com/oauth/token";
    private final RestTemplate restTemplate;

    public String getToken(String clientId, String clientSecret, String code) {
        StravaToken stravaToken = new StravaToken("");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("grant_type", "authorization_code");

        try {
            stravaToken = restTemplate.postForObject(builder.toUriString(), null, StravaToken.class);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return stravaToken.access_token();
    }
}
