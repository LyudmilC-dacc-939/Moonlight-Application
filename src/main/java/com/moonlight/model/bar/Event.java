package com.moonlight.model.bar;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;

    private LocalDateTime eventDate;
    // "MM/dd/yyyy HH:mm:ss" -- in the request, so that events have a specific date AND time

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Screen screen;

}
