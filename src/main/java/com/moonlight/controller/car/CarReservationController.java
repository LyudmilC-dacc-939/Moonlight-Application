package com.moonlight.controller.car;


import com.moonlight.dto.car.CarAvailabilityRequest;
import com.moonlight.dto.car.CarAvailabilityResponse;
import com.moonlight.dto.car.CarReservationRequest;
import com.moonlight.dto.car.CarReservationResponse;
import com.moonlight.model.car.CarReservation;
import com.moonlight.model.user.User;
import com.moonlight.service.CarReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/reservations/car")
@Tag(name = "Car Reservation API", description = "API for managing car reservations")
public class CarReservationController {

    @Autowired
    private CarReservationService carReservationService;

    @Operation(summary = "Car Reservation", description = "Creates a reservation for a car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car reservation successfully made",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarReservation.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarReservation.class))),
            @ApiResponse(responseCode = "403", description = "Given dates are invalid/taken",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarReservation.class))),
            @ApiResponse(responseCode = "404", description = "User or Car not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarReservation.class)))})
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(path = "/create-reservation/")
    public ResponseEntity<CarReservationResponse> createReservation(
            @Valid @RequestBody CarReservationRequest request, @AuthenticationPrincipal User user) {
        String email = user.getEmailAddress();
        CarReservation reservation = carReservationService.createReservation(request, email);

        CarReservationResponse response = new CarReservationResponse(
                reservation.getUser().getId(),
                reservation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getCar().getCarBrand(),
                reservation.getCar().getType().name(),
                reservation.getTotalCost()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check Available Cars", description = "Checks which cars are free for given date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cars are listed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarReservation.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarReservation.class))),
            @ApiResponse(responseCode = "403", description = "Given dates are invalid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarReservation.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarReservation.class)))})
    @GetMapping("/available/")
    public ResponseEntity<CarAvailabilityResponse> getAvailableCars(
            @Valid @RequestBody CarAvailabilityRequest request) {

        Map<LocalDate, List<String>> dailyAvailability = carReservationService.getAvailableCarsByDateRange(request);

        CarAvailabilityResponse response = new CarAvailabilityResponse(dailyAvailability);
        return ResponseEntity.ok(response);
    }
}
