package com.moonlight.controller.restaurant;

import com.moonlight.advice.exception.ItemNotFoundException;
import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant API", description = "API for managing restaurants")
@Validated
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(summary = "Find Tables", description = "Finds a table that fits the criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Restaurant.class))),
            @ApiResponse(responseCode = "204", description = "No tables found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Restaurant.class)))})
    @GetMapping("/search")
    public ResponseEntity<Set<Restaurant>> searchRestaurantTable(
            @RequestParam(value = "tableNumber", required = false) Long tableNumber,
            @RequestParam(value = "restaurantZone", required = false) String restaurantZone) {
        Set<Restaurant> restaurantTables = restaurantService.findByTableNumberOrZone(tableNumber, restaurantZone);
        if (restaurantTables.isEmpty()) {
            throw new ItemNotFoundException("No tables found with your search criteria");
        }
        return ResponseEntity.ok(restaurantTables);
    }
}
