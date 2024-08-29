package com.moonlight.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelRoomReservationResponse {

    private LocalDate startDate;

    private int duration;

    private LocalDate endDate;

    private int guestsAdult;

    private int guestsChildren;

    private String hotelRoomType;

    private double totalCost;
}




