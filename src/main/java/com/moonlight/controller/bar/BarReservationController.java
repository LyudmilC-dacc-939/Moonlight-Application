package com.moonlight.controller.bar;

import com.moonlight.dto.bar.BarReservationRequest;
import com.moonlight.dto.bar.BarReservationResponse;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.user.User;
import com.moonlight.service.BarReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/reservations/bar")
@Tag(name = "Bar Reservation API", description = "API for managing bar reservations")
@Valid
public class BarReservationController {

    @Autowired
    private BarReservationService barReservationService;

    @Operation(summary = "Bar Reservation", description = "Creates a reservation for a bar seat/s")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bar reservation successfully made",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BarReservation.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BarReservation.class))),
            @ApiResponse(responseCode = "403", description = "Given dates are invalid/taken",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BarReservation.class))),
            @ApiResponse(responseCode = "404", description = "User or Bar or Seat or Screen not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BarReservation.class)))})
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(path = "/create-reservation/")
    public ResponseEntity<BarReservationResponse> createReservation(@RequestBody @Valid BarReservationRequest request, @AuthenticationPrincipal User user) {
        BarReservationResponse response = barReservationService.createReservation(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Bar Reservation", description = "Get Available seats per screen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Available seats list successfully made",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BarReservation.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BarReservation.class))),
            @ApiResponse(responseCode = "403", description = "Given dates are invalid/taken",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BarReservation.class))),
            @ApiResponse(responseCode = "404", description = "Seat/s or Screen not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BarReservation.class)))})
    @GetMapping(path = "/available-seats/")
    public ResponseEntity<List<Seat>> listAvailableSeats(
            @RequestParam(name = "screen") String screen,
            @RequestParam(name = "reservationDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate reservationDate) {
        List<Seat> availableSeats = barReservationService.getAvailableSeats(screen, reservationDate);
        return ResponseEntity.ok(availableSeats);
    }
}