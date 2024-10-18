package com.moonlight.controller.restaurant;

import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant API", description = "API for managing restaurants")
@Validated
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(summary = "Find Tables",
            description = "Finds a table that fits the criteria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Match found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Restaurant.class))),
            @ApiResponse(responseCode = "404",
                    description = "No tables found with the specified criteria",
                    content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchRestaurantTable(
            @RequestParam(value = "tableNumber", required = false) Long tableNumber,
            @RequestParam(value = "restaurantZone", required = false) String restaurantZone) {
        Set<Restaurant> restaurantTables = restaurantService.findByTableNumberOrZone(tableNumber, restaurantZone);
        if (restaurantTables.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No tables found with your search criteria.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(restaurantTables);
    }
}
