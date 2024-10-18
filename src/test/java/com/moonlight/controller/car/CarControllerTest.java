package com.moonlight.controller.car;

import com.moonlight.advice.exception.ItemNotFoundException;
import com.moonlight.dto.ImageResponse;
import com.moonlight.model.car.Car;
import com.moonlight.model.enums.CarType;
import com.moonlight.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for the /search/ endpoint
    @Test
    public void testGetCars_Success() {
        // Mock the CarService response
        List<Car> cars = new ArrayList<>();
        Car car = new Car();
        car.setId(1L);
        car.setCarBrand("FERRARI F8 2021");
        car.setType(CarType.SPORT);
        cars.add(car); // Add mock car objects

        when(carService.findByQuerySearch("Toyota", "SUV")).thenReturn(cars);

        // Call the controller method
        ResponseEntity<List<Car>> response = carController.getCars("Toyota", "SUV");

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetCars_ItemNotFound() {
        // Mock an empty list for the CarService response
        when(carService.findByQuerySearch("Toyota", "SUV")).thenReturn(new ArrayList<>());

        // Assert the exception
        assertThrows(ItemNotFoundException.class, () -> carController.getCars("Toyota", "SUV"));
    }

    // Test for the /images endpoint when imageId is provided
    @Test
    public void testGetCarImages_ImageIdProvided() {
        // Mock the ImageResponse object
        List<ImageResponse> images = new ArrayList<>();
        ImageResponse imageResponse = new ImageResponse(1L, null, null);
        imageResponse.setImageData(Base64.getEncoder().encodeToString("mock-image-data".getBytes()));
        images.add(imageResponse);

        // Mock CarService response
        when(carService.getCarImages(null, 1L)).thenReturn(images);

        // Call the controller method
        ResponseEntity<?> response = carController.getCarImages(null, 1L);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertArrayEquals(Base64.getDecoder().decode(imageResponse.getImageData()), (byte[]) response.getBody());
    }

    // Test for the /images endpoint when carId is provided (and no imageId)
    @Test
    public void testGetCarImages_CarIdProvided() {
        // Mock the ImageResponse object
        List<ImageResponse> images = new ArrayList<>();
        images.add(new ImageResponse(null, 1L, null)); // Add mock image response

        // Mock CarService response
        when(carService.getCarImages(1L, null)).thenReturn(images);

        // Call the controller method
        ResponseEntity<?> response = carController.getCarImages(1L, null);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(((List<ImageResponse>) response.getBody()).isEmpty());
    }

    // Test for /images when no image or car found
    @Test
    public void testGetCarImages_NoContent() {
        // Mock an empty list from the CarService
        when(carService.getCarImages(1L, null)).thenReturn(new ArrayList<>());

        // Call the controller method
        ResponseEntity<?> response = carController.getCarImages(1L, null);

        // Assert the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
