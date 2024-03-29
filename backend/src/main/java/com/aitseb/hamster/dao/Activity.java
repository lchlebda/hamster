package com.aitseb.hamster.dao;

import com.aitseb.hamster.dto.StravaActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "strava_id")
    private long stravaId;

    private LocalDateTime date;
    private StravaActivityType sport;
    private String description;
    private int time;

    @Column(name = "rege_time")
    private int regeTime;

    private int hr;

    @Column(name = "hr_max")
    private int hrMax;

    private int cadence;
    private int power;
    private float ef;
    private float tss;
    private int effort;
    private int elevation;
    private float speed;
    private int distance;
    
    @Column(name="notes", length = 1000)
    private String notes;
}
