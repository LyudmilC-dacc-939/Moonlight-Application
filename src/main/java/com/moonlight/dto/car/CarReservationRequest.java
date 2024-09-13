package com.moonlight.dto.car;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CarReservationRequest {
    @NotNull(message = "Car Id can not be null")
    private Long carId;

    @NotNull(message = "Please fill in number of persons")
    @Positive(message = "Positive number is required")
    private int numberOfPeople;

    @NotNull(message = "Please, fill in start date")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "Please fill in end date")
    @Future(message = "End date cannot be in the past")
    private LocalDate endDate;
}
