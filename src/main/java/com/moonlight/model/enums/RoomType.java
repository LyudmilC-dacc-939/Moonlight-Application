package com.moonlight.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {

    STANDARD(220.0, "24 sq. m.",2),
    STUDIO(320.0, "34 sq. m.",3),
    APARTMENT(520.0, "56 sq. m.",4);

    private final Double roomPricePerNight;

    private final String roomArea;

    private final Integer maxNumberOfGuests;

}
