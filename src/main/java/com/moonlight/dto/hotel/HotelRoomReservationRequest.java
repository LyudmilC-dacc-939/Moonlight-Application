package com.moonlight.dto.hotel;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class HotelRoomReservationRequest {

    @NotNull(message = "Please select a room number")
    private Long roomNumber;

    @NotNull(message = "You are required to fill in start date field")
    @FutureOrPresent (message = "Start date can not be in the past")
    private LocalDate startDate;

    @NotNull(message = "You are required to fill in end date field")
    @Future(message = "End date can not be in the past")
    private LocalDate endDate;

    @NotNull(message = "Please, fill in number of guests field")
    private int guestsAdult;

    private int guestsChildren;
}
