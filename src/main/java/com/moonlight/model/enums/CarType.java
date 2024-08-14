package com.moonlight.model.enums;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@Getter
public enum CarType {
    SPORT("Sport car with 2 seats", 2, 1000.0, 3),
    LUXURY("Luxury car with 5 seats", 5, 800.0,4),
    VAN("Van with 8 seats", 8, 600.0,4);

    private final String description;
    @Column(name = "NUMBER_OF_SEATS")
    private final int numberOfSeats;
    private final double price;   // price per car per day
    private final int quantity;  // Available quantity of this car type

    CarType(String description, int numberOfSeats, double price, int quantity) {
        this.description = description;
        this.numberOfSeats = numberOfSeats;
        this.price=price;
        this.quantity=quantity;
    }
}
