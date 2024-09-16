package com.moonlight.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScreenName {
    SCREEN_1,
    SCREEN_2,
    SCREEN_3;

    private final double seatPrice = 10.0;

}
