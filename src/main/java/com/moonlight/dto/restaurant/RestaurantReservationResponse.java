package com.moonlight.dto.restaurant;

import com.moonlight.model.enums.RestaurantZone;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class RestaurantReservationResponse {
    private Long id;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private RestaurantZone zone;
    private Long tableNumber;
    private boolean isSmoking;
    private double totalCost;

}
