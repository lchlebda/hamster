package com.aitseb.hamster.repository;

import com.aitseb.hamster.dao.Activity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ActivitiesRepository extends CrudRepository<Activity, Long> {
    Activity findFirstByOrderByDateDesc();

    @Modifying
    @Transactional
    @Query("update Activity a set a.time = :time where a.id = :id")
    void updateTime(@Param(value = "id") long id, @Param(value = "time") Integer time);

    @Modifying
    @Transactional
    @Query("update Activity a set a.description = :description where a.id = :id")
    void updateDescription(@Param(value = "id") long id, @Param(value = "description") String description);
}
