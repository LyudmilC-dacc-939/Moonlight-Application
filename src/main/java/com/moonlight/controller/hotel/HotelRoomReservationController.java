package com.moonlight.controller.hotel;

import com.moonlight.dto.hotel.HotelRoomAvailabilityRequest;
import com.moonlight.dto.hotel.HotelRoomAvailabilityResponse;
import com.moonlight.dto.hotel.HotelRoomReservationRequest;
import com.moonlight.dto.hotel.HotelRoomReservationResponse;
import com.moonlight.model.hotel.HotelRoomReservation;
import com.moonlight.model.user.User;
import com.moonlight.service.impl.hotel.HotelRoomReservationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/reservations/hotel")
@RequiredArgsConstructor
@Tag(name = "Hotel API", description = "API for hotel management")
@Data
public class HotelRoomReservationController {

    @Autowired
    private HotelRoomReservationServiceImpl hotelRoomReservationService;

    @Operation(summary = "Hotel room Reservation", description = "Creates a reservation for a hotel room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hotel room reservation successfully made",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HotelRoomReservation.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HotelRoomReservation.class))),
            @ApiResponse(responseCode = "404", description = "User or hotel room not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HotelRoomReservation.class)))})
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(path = "/create-reservation/")
    public ResponseEntity<HotelRoomReservationResponse> createReservation
            (@RequestBody @Valid HotelRoomReservationRequest reservationRequest, @AuthenticationPrincipal User user) {


        HotelRoomReservation reservation = hotelRoomReservationService.makeReservation(
                user.getId(),
                reservationRequest.getRoomNumber(),
                reservationRequest.getStartDate(),
                reservationRequest.getEndDate(),
                reservationRequest.getGuestsAdult(),
                reservationRequest.getGuestsChildren()
        );

        HotelRoomReservationResponse response = new HotelRoomReservationResponse();
        response.setStartDate(reservation.getStartDate());
        response.setDuration(reservation.getDuration());
        response.setEndDate(reservation.getEndDate());
        response.setGuestsAdult(reservation.getGuestsAdult());
        response.setGuestsChildren(reservation.getGuestsChildren());
        response.setHotelRoomType(reservation.getHotelRoom().getRoomType().name());
        response.setTotalCost(reservation.getTotalCost());


        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "List available hotel rooms"
            , description = "Generate a list for hotel rooms available for the selected period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"
                    , description = "List of available accommodation successfully generated.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HotelRoomReservation.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HotelRoomReservation.class))),
            @ApiResponse(responseCode = "404", description = "List of available hotel rooms not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HotelRoomReservation.class)))})
    @GetMapping(path = "/available-rooms/")
    public ResponseEntity<List<HotelRoomAvailabilityResponse>> getAvailableRooms
            (@RequestBody @Validated HotelRoomAvailabilityRequest request) {

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        List<HotelRoomAvailabilityResponse> availableRooms =
                hotelRoomReservationService.getAvailableRooms(startDate, endDate);

        return new ResponseEntity<>(availableRooms, HttpStatus.OK);
    }


}
