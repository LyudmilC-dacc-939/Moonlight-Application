package com.moonlight.service.impl.car;

import com.moonlight.model.car.Car;
import com.moonlight.model.enums.CarType;
import com.moonlight.repository.car.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carServiceImpl;

    private Car car1;
    private Car car2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        car1 = new Car();
        car1.setCarBrand("Toyota");
        car1.setType(CarType.SPORT);

        car2 = new Car();
        car2.setCarBrand("Honda");
        car2.setType(CarType.VAN);
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

}