package com.moonlight.dto.bar;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.moonlight.serialization.DateOrDateTimeDeserializer;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddEventRequest {

        @NotNull(message = "Event must have name")
        private String eventName;

        @NotNull(message = "Event must be in correct format: \n1. dd-MM-yyyy HH:mm:ss \n2.dd-MM-yyyy")
        @JsonDeserialize(using = DateOrDateTimeDeserializer.class)
        private LocalDateTime eventDate;
        // can use both dd-MM-yyyy HH:mm:ss AND dd-MM-yyyy when making a request: in the first instance it will
        // create the event for a specific date and time, for the second it will save the date and automatically assign time

        @NotNull(message = "Event must have a screen")
        private int screenId;
}
