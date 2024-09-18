package com.moonlight.dto.restaurant;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class TableAvailabilityRequest {
    @FutureOrPresent(message = "Date and time should not be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate reservationDate; // if date is not provided will take by default today
    @FutureOrPresent(message = "Date and time should not be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;
    @FutureOrPresent(message = "Date and time should not be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;
    @Positive(message = "Seats provided shall be positive  number")
    private Integer seats; // for filtering available tables based on visitors
    private Boolean isSmoking; // for filtering either for smokers or non smokers
}
