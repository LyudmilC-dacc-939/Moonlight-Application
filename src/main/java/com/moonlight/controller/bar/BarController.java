package com.moonlight.controller.bar;

import com.moonlight.advice.exception.ItemNotFoundException;
import com.moonlight.model.bar.Bar;
import com.moonlight.model.bar.Seat;
import com.moonlight.service.BarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
