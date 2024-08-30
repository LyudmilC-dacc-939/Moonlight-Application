package com.moonlight.service.impl.car;

import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.ImageResponse;
import com.moonlight.model.car.Car;
import com.moonlight.model.car.FileResource;
import com.moonlight.model.enums.CarType;
import com.moonlight.repository.car.CarRepository;
import com.moonlight.repository.car.FileResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private FileResourceRepository fileResourceRepository;

    @InjectMocks
    private CarServiceImpl carServiceImpl;

    private Car car1;
    private Car car2;
    private FileResource fileResource;
    private List<FileResource> fileResources;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Method One setUp data
        car1 = new Car();
        car1.setCarBrand("Toyota");
        car1.setType(CarType.SPORT);

        car2 = new Car();
        car2.setCarBrand("Honda");
        car2.setType(CarType.VAN);

        // Method Two setUp data
        // Setup common FileResource object
        fileResource = new FileResource();
        fileResource.setId(1L);
        fileResource.setDataValue(new byte[]{1, 2, 3});
        fileResource.setCar(new Car());
        fileResource.getCar().setId(10L);

        // Setup list of FileResources (3 images for the car)
        fileResources = new ArrayList<>();
        fileResources.add(fileResource);
        FileResource secondImage = new FileResource();
        secondImage.setId(2L);
        secondImage.setDataValue(new byte[]{4, 5, 6});
        secondImage.setCar(new Car());
        secondImage.getCar().setId(10L);
        FileResource thirdImage = new FileResource();
        thirdImage.setId(3L);
        thirdImage.setDataValue(new byte[]{7, 8, 9});
        thirdImage.setCar(new Car());
        thirdImage.getCar().setId(10L);

        fileResources.add(secondImage);
        fileResources.add(thirdImage);
    }

    @Test
    public void testFindByQuerySearch_ReturnsMatchingCars() {
        String carType = CarType.SPORT.toString();
        String carBrand = "Toyota";

        when(carRepository.findByCarBrandOrType(carType, carBrand))
                .thenReturn(Arrays.asList(car1, car2));

        List<Car> result = carRepository.findByCarBrandOrType(carType, carBrand);

        assertEquals(2, result.size());
        assertEquals("Toyota", result.get(0).getCarBrand());
        assertEquals(CarType.SPORT, result.get(0).getType());

        assertEquals("Honda", result.get(1).getCarBrand());
        assertEquals(CarType.VAN, result.get(1).getType());
    }

    @Test
    public void testFindByQuerySearch_NoMatchingCars() {
        String carType = CarType.SPORT.toString();
        String carBrand = "Ford";

        List<Car> result = carServiceImpl.findByQuerySearch(carType, carBrand);

        assertEquals(0, result.size());
    }

    // Method Two
    @Test
    void testGetCarImagesWithValidCarIdAndImageId() {
        when(fileResourceRepository.findByCarId(10L)).thenReturn(fileResources);
        when(fileResourceRepository.findById(1L)).thenReturn(Optional.of(fileResource));

        List<ImageResponse> responses = carServiceImpl.getCarImages(10L, 1L);

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getImageId());
        assertEquals(10L, responses.get(0).getCarId());
        assertEquals(Base64.getEncoder().encodeToString(fileResource.getDataValue()), responses.get(0).getImageData());
    }

    @Test
    void testGetCarImagesWithValidCarIdAndInvalidImageId() {
        when(fileResourceRepository.findByCarId(10L)).thenReturn(fileResources);

        UnavailableResourceException thrown = assertThrows(UnavailableResourceException.class, () -> {
            carServiceImpl.getCarImages(10L, 4L);
        });

        assertEquals("Image ID 4 does not belong to Car ID 10.", thrown.getMessage());
    }

    @Test
    void testGetCarImagesWithValidCarIdOnly() {
        when(fileResourceRepository.findByCarId(10L)).thenReturn(fileResources);

        List<ImageResponse> responses = carServiceImpl.getCarImages(10L, null);

        assertEquals(3, responses.size());
        assertTrue(responses.stream().anyMatch(r -> r.getImageId().equals(1L)));
        assertTrue(responses.stream().anyMatch(r -> r.getImageId().equals(2L)));
        assertTrue(responses.stream().anyMatch(r -> r.getImageId().equals(3L)));
    }

    @Test
    void testGetCarImagesWithInvalidCarId() {
        when(fileResourceRepository.findByCarId(10L)).thenReturn(Collections.emptyList());

        RecordNotFoundException thrown = assertThrows(RecordNotFoundException.class, () -> {
            carServiceImpl.getCarImages(10L, null);
        });

        assertEquals("Car with ID 10 does not exist.", thrown.getMessage());
    }

    // The line is covered, I don't understand why IJ does not recognize it...
    @Test
    void testGetCarAllImagesForCarWithInvalidCarId() {
        when(fileResourceRepository.findByCarId(13L)).thenReturn(Collections.emptyList());

        RecordNotFoundException thrown = assertThrows(RecordNotFoundException.class, () -> {
            carServiceImpl.getCarImages(13L, null);
        });

        assertEquals("Car with ID 13 does not exist.", thrown.getMessage());
    }

    @Test
    void testGetCarImagesWithImageIdOnly() {
        when(fileResourceRepository.findById(1L)).thenReturn(Optional.of(fileResource));

        List<ImageResponse> responses = carServiceImpl.getCarImages(null, 1L);

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getImageId());
        assertEquals(10L, responses.get(0).getCarId());
        assertEquals(Base64.getEncoder().encodeToString(fileResource.getDataValue()), responses.get(0).getImageData());
    }

    @Test
    void testGetCarImagesWithInvalidImageId() {
        when(fileResourceRepository.findById(1L)).thenReturn(Optional.empty());

        List<ImageResponse> responses = carServiceImpl.getCarImages(null, 1L);

        assertTrue(responses.isEmpty());
    }

}