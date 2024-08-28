package com.moonlight.dto.hotel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelRoomAvailabilityResponse {

    private Long roomNumber;
    private String roomType;
    private String roomView;
    private String roomBedType;
    private double roomPricePerNight;
    private int maxNumberOfGuests;

}
