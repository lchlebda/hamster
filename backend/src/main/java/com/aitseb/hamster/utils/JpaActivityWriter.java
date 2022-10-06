package com.aitseb.hamster.utils;

import com.aitseb.hamster.dao.Activity;
import com.aitseb.hamster.repository.ActivitiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaActivityWriter implements ItemWriter<Activity> {

    private final ActivitiesRepository activitiesRepository;

    @Override
    public void write(List<? extends Activity> list) {
        for (Activity activity : list) {
            activitiesRepository.save(activity);
        }
    }
}
