package com.moonlight.dto.hotel;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HotelRoomAvailabilityRequest {

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @Future(message = "End date cannot be in the past")
    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;
}
