package com.moonlight.dto.bar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarReservationResponse {
    private Long userId;
    private Long reservationId;
    private Set<Integer> seatNumbers;
    private Long screenId;
    private String eventName;
    private Double totalCost;
    private LocalDate reservationDate;
    private String userName;
}