package com.moonlight.model.restaurant;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moonlight.model.enums.RestaurantZone;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "RESTAURANTS")
@Table(name = "restaurants")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @Column(name = "restaurant_zone")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please select a zone to be seated in.")
    private RestaurantZone restaurantZone;

    @NotNull(message = "Please select a table number")
    @Column(name = "table_number")
    private Long tableNumber;

    @Positive
    @NotNull(message = "Please enter the maximum number of seats for this table")
    private int maxNumberOfSeats;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<RestaurantReservation> restaurantReservations;

}
