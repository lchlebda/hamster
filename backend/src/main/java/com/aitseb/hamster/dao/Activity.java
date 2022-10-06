package com.aitseb.hamster.dao;

import com.aitseb.hamster.dto.StravaActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "strava_id")
    private Long stravaId;

    private LocalDate date;
    private StravaActivityType sport;
    private String description;
    private Integer time;

    @Column(name = "rege_time")
    private Integer regeTime;

    private Integer hr;

    @Column(name = "hr_max")
    private Integer hrMax;

    private Integer cadence;
    private Integer power;
    private Float ef;
    private Float tss;
    private Integer effort;
    private Integer elevation;
    private Float speed;
    private Float distance;
    private String notes;
}
