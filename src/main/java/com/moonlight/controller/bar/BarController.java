package com.moonlight.controller.bar;

import com.moonlight.advice.exception.ItemNotFoundException;
import com.moonlight.dto.bar.AddEventRequest;
import com.moonlight.dto.bar.AddEventResponse;
import com.moonlight.dto.bar.ScreenInformationResponse;
import com.moonlight.model.bar.Bar;
import com.moonlight.model.bar.Event;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import com.moonlight.service.BarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/bars")
@RequiredArgsConstructor
@Tag(name = "Bars API", description = "API for managing bars")
@Valid
public class BarController {

    private final BarService barService;

    @Operation(summary = "Find Bar seats for specific screen", description = "Finds bar seats that fits the criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Bar.class))),
            @ApiResponse(responseCode = "204", description = "No seats found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Bar.class)))})
    @GetMapping("/search/screen")
    public ResponseEntity<Set<Seat>> searchByScreen(@RequestParam(value = "screenName") String screenName) {
        Set<Seat> barSeats = barService.searchByScreen(screenName);
        if (barSeats.isEmpty()) {
            throw new ItemNotFoundException("No seats found with your search criteria");
        }
        return ResponseEntity.ok(barSeats);
    }

    @Operation(summary = "Find Bar seats by specific screen and specific seat number", description = "Finds a bar seat that fits the criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Bar.class))),
            @ApiResponse(responseCode = "204", description = "No seats found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Bar.class)))})
    @GetMapping("/search/screen/seats")
    public ResponseEntity<Set<Seat>> searchByScreenAndBySeatNumber(@RequestParam(value = "screenName") String screenName, @RequestParam(value = "seatNumber") Long seatNumber) {
        Set<Seat> barSeats = barService.searchBySeatNumberAndByScreen(screenName, seatNumber);
        if (barSeats.isEmpty()) {
            throw new ItemNotFoundException("No seats found with your search criteria");
        }
        return ResponseEntity.ok(barSeats);
    }

    @Operation(
            summary = "Create a new event",
            description = "Creates a new event with the provided details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Event created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddEventResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/events/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AddEventResponse> createEvent(@Valid @RequestBody AddEventRequest addEventRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(barService.createEvent(addEventRequest));
    }

    @Operation(summary = "Search for events",
            description = "Retrieve a list of events based on optional search criteria such as event ID, name, or date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302",
                    description = "Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content)
    })
    @GetMapping("/events/search/")
    public ResponseEntity<List<Event>> getEvents(@RequestParam(value = "eventId", required = false) Long eventId,
                                                 @RequestParam(value = "eventName", required = false) String eventName,
                                                 @RequestParam(value = "eventDate", required = false)
                                                 @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate eventDate
    ) {
        return ResponseEntity.status(HttpStatus.FOUND).body(barService.findByQuerySearch(eventId, eventName, eventDate));
    }

    @Operation(summary = "Get information for a specific screen",
            description = "Retrieve detailed information for a specified screen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302",
                    description = "Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScreenInformationResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid screen identifier",
                    content = @Content)
    })
    @GetMapping("/screen/get-info/")
    public ResponseEntity<ScreenInformationResponse> getScreenInfo(@RequestParam(value = "screenEnum") Screen screenEnum) {
        return ResponseEntity.status(HttpStatus.FOUND).body(barService.getFullInfoForScreen(screenEnum));
    }
}
