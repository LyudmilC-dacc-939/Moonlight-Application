package com.moonlight.dto.car;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
public class CarAvailabilityResponse {
    private Map<LocalDate, List<String>> dailyAvailability;

    public CarAvailabilityResponse(Map<LocalDate, List<String>> dailyAvailability) {
        this.dailyAvailability = dailyAvailability;
    }
}
