package com.moonlight.controller.restaurant;

import com.moonlight.dto.restaurant.RestaurantReservationRequest;
import com.moonlight.dto.restaurant.RestaurantReservationResponse;
import com.moonlight.dto.restaurant.TableAvailabilityResponse;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;
import com.moonlight.service.RestaurantReservationService;
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
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/reservations/restaurant")
@Tag(name = "Restaurant Reservation API", description = "API for managing restaurant reservations")
@Valid
public class RestaurantReservationController {
    @Autowired
    private RestaurantReservationService restaurantReservationService;

    @PostMapping("/create-reservation/")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Restaurant reservation", description = "Creates a reservation for a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant reservation successfully made",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantReservation.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantReservation.class))),
            @ApiResponse(responseCode = "403", description = "Given dates are invalid/taken",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantReservation.class))),
            @ApiResponse(responseCode = "404", description = "User or Restaurant not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantReservation.class)))})
    public ResponseEntity<RestaurantReservationResponse> createReservation(
            @Valid @RequestBody RestaurantReservationRequest request, @AuthenticationPrincipal User user) {
        RestaurantReservation reservation = restaurantReservationService.createReservation(request, user);
        RestaurantReservationResponse response = new RestaurantReservationResponse(
                reservation.getId(),
                reservation.getReservationDate(),
                reservation.getReservationTime().toLocalTime(),
                reservation.getZone(),
                reservation.getTableNumber(),
                reservation.isSmoking(),
                reservation.getSeatCost());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-tables")
    @Operation(summary = "Get available tables", description = "Get available tables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Available tables successfully made",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantReservation.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantReservation.class))),
            @ApiResponse(responseCode = "403", description = "Given dates are invalid/taken",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantReservation.class))),
            @ApiResponse(responseCode = "404", description = "User or Restaurant not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantReservation.class)))})
    public ResponseEntity<?> getAvailableTables(
            @RequestParam(required = false)
            LocalDate reservationDate,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            @RequestParam(required = false) Integer seats,
            @RequestParam(required = false) Boolean isSmoking
    ) {
        TableAvailabilityResponse response = restaurantReservationService
                .getAvailableTablesByDateAndPreferences(reservationDate, startTime, endTime, seats, isSmoking);

        return ResponseEntity.ok(response);
    }
}
