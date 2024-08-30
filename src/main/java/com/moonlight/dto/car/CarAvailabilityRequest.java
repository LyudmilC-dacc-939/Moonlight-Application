package com.moonlight.dto.car;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CarAvailabilityRequest {
    @NotNull(message = "Please, fill in start date")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;
    @NotNull(message = "Please fill in end date")
    @Future(message = "End date cannot be in the past")
    private LocalDate endDate;
}
