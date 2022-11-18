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
    @Query("update Activity a set a.description = :description where a.id = :id")
    void updateDescription(@Param(value = "id") long id, @Param(value = "description") String description);

    @Modifying
    @Transactional
    @Query("update Activity a set a.time = :time where a.id = :id")
    void updateTime(@Param(value = "id") long id, @Param(value = "time") Integer time);

    @Modifying
    @Transactional
    @Query("update Activity a set a.regeTime = :regeTime where a.id = :id")
    void updateRegeTime(@Param(value = "id") long id, @Param(value = "regeTime") Integer regeTime);

    @Modifying
    @Transactional
    @Query("update Activity a set a.hr = :hr where a.id = :id")
    void updateHr(@Param(value = "id") long id, @Param(value = "hr") Integer hr);

    @Modifying
    @Transactional
    @Query("update Activity a set a.hrMax = :hrMax where a.id = :id")
    void updateHrMax(@Param(value = "id") long id, @Param(value = "hrMax") Integer hrMax);

    @Modifying
    @Transactional
    @Query("update Activity a set a.cadence = :cadence where a.id = :id")
    void updateCadence(@Param(value = "id") long id, @Param(value = "cadence") Integer cadence);

    @Modifying
    @Transactional
    @Query("update Activity a set a.power = :power where a.id = :id")
    void updatePower(@Param(value = "id") long id, @Param(value = "power") Integer power);

    @Modifying
    @Transactional
    @Query("update Activity a set a.ef = :ef where a.id = :id")
    void updateEf(@Param(value = "id") long id, @Param(value = "ef") Float ef);

    @Modifying
    @Transactional
    @Query("update Activity a set a.tss = :tss where a.id = :id")
    void updateTSS(@Param(value = "id") long id, @Param(value = "tss") Float tss);

    @Modifying
    @Transactional
    @Query("update Activity a set a.effort = :effort where a.id = :id")
    void updateEffort(@Param(value = "id") long id, @Param(value = "effort") Integer effort);

    @Modifying
    @Transactional
    @Query("update Activity a set a.elevation = :elevation where a.id = :id")
    void updateElevation(@Param(value = "id") long id, @Param(value = "elevation") Integer elevation);

    @Modifying
    @Transactional
    @Query("update Activity a set a.speed = :speed where a.id = :id")
    void updateSpeed(@Param(value = "id") long id, @Param(value = "speed") Float speed);

    @Modifying
    @Transactional
    @Query("update Activity a set a.notes = :notes where a.id = :id")
    void updateNotes(@Param(value = "id") long id, @Param(value = "notes") String notes);

}
