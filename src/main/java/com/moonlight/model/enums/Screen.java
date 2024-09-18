package com.moonlight.model.enums;

import lombok.Getter;

@Getter
public enum Screen {
    SCREEN_ONE("SCREEN_ONE"),
    SCREEN_TWO("SCREEN_TWO"),
    SCREEN_THREE("SCREEN_THREE");

    private final String screenName;

    Screen(String screenName) {
        this.screenName = screenName;
    }

    @Override
    public String toString() {
        return screenName;
    }
}
