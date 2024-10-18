package com.moonlight.model.restaurant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.enums.RestaurantZone;
import com.moonlight.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant_reservations")
@Getter
@Setter
@NoArgsConstructor
public class RestaurantReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonBackReference
    private Restaurant restaurant;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;

    @Column(name = "reservation_end_time", nullable = false)
    private LocalDateTime reservationEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "zone", nullable = false)
    private RestaurantZone zone;

    @Column(name = "table_number", nullable = false)
    private Long tableNumber;

    @Column(name = "is_smoking", nullable = false)
    private boolean isSmoking;

    @Column(name = "seat_cost", nullable = false)
    private double seatCost;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String paymentIntentId;
}
