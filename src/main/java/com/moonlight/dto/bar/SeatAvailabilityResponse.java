package com.moonlight.dto.bar;

import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class SeatAvailabilityResponse {
    private  int seatNumber;
    private String screenName;
}
