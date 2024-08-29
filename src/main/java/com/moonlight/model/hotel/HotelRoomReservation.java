package com.moonlight.model.hotel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.moonlight.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "room_reservations")
@NoArgsConstructor
@Data
public class HotelRoomReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "roomNumber", nullable = false)
    @JsonBackReference
    private HotelRoom hotelRoom;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private double totalCost;

    @Column(nullable = false)
    @Min(value = 1, message = "Number of adult guests must be at least {value}")
    @Max(value = 4, message = "Number of adult guests must be at most {value}")
    private int guestsAdult;

    @Column(nullable = false)
    @Min(value = 0, message = "Number of children guests must be at least {value}")
    @Max(value = 4, message = "Number of adult guests must be at most {value}")
    private int guestsChildren;
}
