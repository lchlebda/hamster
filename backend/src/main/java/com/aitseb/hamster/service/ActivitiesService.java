package com.aitseb.hamster.service;

import com.aitseb.hamster.dto.StravaActivity;
import com.aitseb.hamster.repository.ActivitiesRepository;
import com.aitseb.hamster.utils.ActivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class ActivitiesService {

    private final ActivitiesRepository repository;

    public void save(StravaActivity activity) {
        repository.save(ActivityMapper.fromStravaToDAO(activity));
    }
}
