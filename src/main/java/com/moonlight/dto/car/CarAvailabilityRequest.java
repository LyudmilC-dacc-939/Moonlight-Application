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
    @NotNull
    @FutureOrPresent
    private LocalDate startDate;
    @NotNull
    @Future
    private LocalDate endDate;
}
