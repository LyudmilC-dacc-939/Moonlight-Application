package com.moonlight.model.bar;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.moonlight.model.enums.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_name")
    @NotNull
    private String eventName;

    @Column(name = "event_date")
    @NotNull
    @Future
    private LocalDateTime eventDate;
    // "MM/dd/yyyy HH:mm:ss" -- in the request, so that events have a specific date AND time

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Event must have a screen")
    private Screen screen;

}
