package com.aitseb.hamster.repository;

import com.aitseb.hamster.dao.Activity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivitiesRepository extends CrudRepository<Activity, Long> { }
