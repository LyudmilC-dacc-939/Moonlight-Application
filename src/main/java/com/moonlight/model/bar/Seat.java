package com.moonlight.model.bar;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.moonlight.model.enums.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SEATS")
@Data
@NoArgsConstructor
public class Seat {

    private final double seatPrice = 5.0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Seat must have a screen")
    private Screen screen;
    @Column(name = "seat_number")
    @NotNull(message = "Seat must have a number")
    private int seatNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bar_id", nullable = false)
    @JsonBackReference
    private Bar bar;

}