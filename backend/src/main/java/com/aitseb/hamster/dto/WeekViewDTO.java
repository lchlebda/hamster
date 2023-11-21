package com.aitseb.hamster.dto;

import java.util.List;

public record WeekViewDTO(List<ActivityDTO> activities, List<WeekSummary> weekSummaries) { }
