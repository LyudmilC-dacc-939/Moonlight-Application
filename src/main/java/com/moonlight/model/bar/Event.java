package com.moonlight.model.bar;

import com.moonlight.model.enums.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_name")
    @NotNull(message = "Event must have name")
    private String eventName;

    @Column(name = "event_date")
    @NotNull(message = "event must have date and time")
    @Future
    private LocalDateTime eventDate;
    // "MM/dd/yyyy HH:mm:ss" -- in the request, so that events have a specific date AND time

    @ElementCollection(targetClass = Screen.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "screen_events", joinColumns = @JoinColumn(name = "event_id"))
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Event must have at least 1 screen")
    private Set<Screen> screen;

}
