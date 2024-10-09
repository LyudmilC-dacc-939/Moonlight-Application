package com.moonlight.controller.car;

import com.moonlight.advice.exception.ItemNotFoundException;
import com.moonlight.dto.ImageResponse;
import com.moonlight.model.car.Car;
import com.moonlight.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
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
            throw new ItemNotFoundException("Car brand: " + carBrand + ", and / or " + carType + " is not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(foundCars);
    }

    @Operation(summary = "Get Car images by Car ID or Image ID", description = "Returns list of car pictures")
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
    @GetMapping("/images")
    public ResponseEntity<?> getCarImages(
            @RequestParam(value = "carId", required = false) Long carId,
            @RequestParam(value = "imageId", required = false) Long imageId) {

        List<ImageResponse> images = carService.getCarImages(carId, imageId);

        if (images.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else if (imageId != null) {
            // If imageId is provided, return a single image
            ImageResponse image = images.get(0);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // Adjust based on your image type
                    .body(Base64.getDecoder().decode(image.getImageData()));
        } else if (carId != null && !images.isEmpty()) {
            // If only carId is provided, return all images as a list of ImageResponseDTOs
            return ResponseEntity.ok(images);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Default fallback
    }
}
