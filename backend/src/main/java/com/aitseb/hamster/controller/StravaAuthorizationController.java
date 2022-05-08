package com.aitseb.hamster.controller;

import com.aitseb.hamster.service.StravaAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/strava/oauth")
@RequiredArgsConstructor
public class StravaAuthorizationController {

    private final StravaAuthorizationService stravaAuthorizationService;

    @GetMapping
    public String getToken(@RequestParam String clientId,
                           @RequestParam String clientSecret,
                           @RequestParam String code) {
        return stravaAuthorizationService.getToken(clientId, clientSecret, code);
    }
}
