package com.moonlight.dto.bar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SeatAvailabilityResponse {
    private int seatNumber;
    private String screenName;
}