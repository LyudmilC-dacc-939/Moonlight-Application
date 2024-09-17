package com.moonlight.model.bar;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String eventName;

    @NotNull
    private LocalDateTime eventDate;
    // "MM/dd/yyyy HH:mm:ss" -- in the request, so that events have a specific date AND time

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JsonBackReference
    private Screen screen;

}
