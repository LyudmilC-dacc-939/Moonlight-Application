package com.moonlight.model.enums;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum Screen {
    SCREEN_ONE(1, "SCREEN: Football"),
    SCREEN_TWO(2, "SCREEN: Tennis"),
    SCREEN_THREE(3, "SCREEN: Formula 1");

    @Column(name = "screen_id")
    private final int id;
    @Column(name = "default_name")
    private final String defaultScreenName;
    @Column(name = "current_name")
    private String currentScreenName;


    public static Screen fromId(int id) {
        for (Screen screen : Screen.values()) {
            if (screen.getId() == id) {
                return screen;
            }
        }
        throw new IllegalArgumentException("Unknown Screen ID: " + id);
    }

    public String getCurrentScreenName(){
        if (this.currentScreenName == null){
            this.currentScreenName = this.defaultScreenName;
            return currentScreenName;
        }
        return currentScreenName;
    }

    public void setScreenNameForEvent(String eventName) {
        this.currentScreenName = eventName;
    }

    public void resetToDefault() {
        this.currentScreenName = this.defaultScreenName;
    }
}
