package com.moonlight.dto.car;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CarReservationResponse {

    private Long reservationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String carBrand;
    private String carType;
    private double totalCost;

}
