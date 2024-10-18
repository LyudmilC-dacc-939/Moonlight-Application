package com.moonlight.dto.restaurant;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class RestaurantReservationRequest {
    @NotNull(message = "Reservation date is required")
    private LocalDate reservationDate;

    @NotNull(message = "Reservation time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime reservationTime;

    @NotNull(message = "Please fill in number of persons")
    @Positive(message = "Positive number is required")
    private int numberOfPeople;


    @NotNull(message = "Table number is required")
    private Long tableNumber;

    @NotNull(message = "Smoking preference is required")
    private boolean isSmoking;
}
