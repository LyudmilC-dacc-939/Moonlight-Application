package com.moonlight.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class TableAvailabilityResponse {
    private LocalDate queryForDate;
    private Map<String, List<String[]>> availableTables;
}