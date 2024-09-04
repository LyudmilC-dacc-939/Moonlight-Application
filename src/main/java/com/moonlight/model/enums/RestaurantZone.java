package com.moonlight.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RestaurantZone {

    BAR(10.00, false),
    SALOON(10.00, false),
    TERRACE(10.00, true);

    private final double seatPrice;

    private final boolean isSmokerArea;

}
