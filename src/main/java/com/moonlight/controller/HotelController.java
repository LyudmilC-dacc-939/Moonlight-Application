package com.moonlight.controller;

import com.moonlight.model.HotelRoom;
import com.moonlight.model.User;
import com.moonlight.service.HotelRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("api/v1/hotel")
@RequiredArgsConstructor
@Tag(name = "Hotel API", description = "API for hotel management")
public class HotelController {

    private final HotelRoomService hotelRoomService;

    @Operation(summary = "Find Rooms", description = "Finds a room that fits the criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HotelRoom.class))),
            @ApiResponse(responseCode = "204", description = "No rooms found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HotelRoom.class)))})
    @GetMapping("/search")
    public ResponseEntity<Set<HotelRoom>> searchHotelRooms(
            @RequestParam(value = "roomNumber", required = false) Long roomNumber,
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestParam(value = "roomView", required = false) String roomView,
            @RequestParam(value = "bedType", required = false) String bedType) {
        Set<HotelRoom> hotelRooms = hotelRoomService.findByRoomNumberByRoomTypeOrViewTypeOrBedType(roomNumber, roomType, roomView, bedType);
        if (hotelRooms.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(hotelRooms);
    }
}
