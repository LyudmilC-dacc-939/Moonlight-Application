package com.moonlight.model.bar;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.enums.Screen;
import com.moonlight.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Table(name = "bar_reservations")
@Entity
@Data
@NoArgsConstructor
public class BarReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "reservation_seats",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    @NotNull(message = "At least one seat must be selected for reservation")
    private Set<Seat> seats;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "screen")
    @NotNull(message = "Screen must be selected")
    private Screen screen;

    @Column(name = "total_cost", nullable = false)
    private double totalCost;

    @Column(name = "reservation_date")
    @NotNull(message = "Reservation must have a date")
    private LocalDate reservationDate;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @NotNull(message = "Event must be selected")
    private Event event;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
}