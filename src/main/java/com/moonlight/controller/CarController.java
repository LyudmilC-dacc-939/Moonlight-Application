package com.moonlight.controller;

import com.moonlight.model.Car;
import com.moonlight.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/cars")
@RequiredArgsConstructor
@Tag(name = "Cars API", description = "API for managing cars")
public class CarController {

    private final CarService carService;

    @Operation(summary = "Find Cars By Query", description = "Returns list of cars by type and/or brand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Car.class))),
            @ApiResponse(responseCode = "400", description = "Format invalid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Car.class))),
            @ApiResponse(responseCode = "204", description = "No content found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Car.class)))})
    @GetMapping(path = "/search/")
    public ResponseEntity<List<Car>> getCars(@RequestParam(value = "carBrand", required = false) String carBrand,
                                             @RequestParam(value = "carType", required = false) String carType) {
        List<Car> foundCars = carService.findByQuerySearch(carBrand, carType);
        if (foundCars.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(foundCars);
    }
}
