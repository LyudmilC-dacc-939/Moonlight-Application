package com.moonlight.dto.bar;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SeatAvailabilityRequest {
    @NotNull(message = "Screen must be selected")
    private String screenName;
    @NotNull(message = "Reservation date is required")
    @FutureOrPresent
    private LocalDate reservationDate;
}