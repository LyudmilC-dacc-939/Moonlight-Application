package com.moonlight.dto.bar;

import com.moonlight.model.bar.Event;
import com.moonlight.model.enums.Screen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenInformationResponse {

    private Screen screen;
    private String defaultScreenName;
    private String currentScreenName;
    private List<Event> eventsForScreen;
}
