package com.moonlight.dto.bar;

import com.moonlight.model.enums.Screen;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class AddEventResponse {

    private Long id;

    private String eventName;

    private LocalDateTime eventDate;

    private String screenDefaultName;
}
