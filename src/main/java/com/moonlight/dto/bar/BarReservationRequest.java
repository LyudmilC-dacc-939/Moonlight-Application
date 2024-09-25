package com.moonlight.dto.bar;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
public class BarReservationRequest {
    @NotNull(message = "At least one seat must be selected")
    @Size(min = 1, max = 21, message = "You can select between 1 and 21 seats")
    private Set<Integer> seatNumbers;  // Seat numbers provided by the user

    @NotNull(message = "Screen must be selected")
    private String screenName;  // The name of the screen selected by the user

    @NotNull(message = "Reservation date is required")
    private LocalDate reservationDate;
}
