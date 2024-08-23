package com.moonlight.controller.car;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/reservations/car")
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
    @PostMapping(path = "/create-reservation/")
    public ResponseEntity<CarReservationResponse> createReservation(
            @RequestBody CarReservationRequest request, @AuthenticationPrincipal User user) {
        String email = user.getEmailAddress();
        CarReservation reservation = carReservationService.createReservation(request, email);

        CarReservationResponse response = new CarReservationResponse(
                reservation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getCar().getCarBrand(),
                reservation.getCar().getType().name(),
                reservation.getTotalCost()
        );

        return ResponseEntity.ok(response);
    }
}
