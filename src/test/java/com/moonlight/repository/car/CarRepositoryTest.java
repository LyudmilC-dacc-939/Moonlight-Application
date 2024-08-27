package com.moonlight.repository.car;

import com.moonlight.model.car.Car;
import com.moonlight.model.enums.CarType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    private Car car1;
    private Car car2;

    @BeforeEach
    void setUp() {
        car1 = new Car();
        car1.setCarBrand("Toyota");
        car1.setType(CarType.SPORT);

        car2 = new Car();
        car2.setCarBrand("Honda");
        car2.setType(CarType.VAN);

        carRepository.save(car1);
        carRepository.save(car2);
    }

    @Test
    public void testFindByCarBrandOrType_WithBrand() {
        List<Car> cars = carRepository.findByCarBrandOrType("Toyota", null);
        assertEquals(1, cars.size());
        assertEquals("Toyota", cars.get(0).getCarBrand());
    }

    @Test
    public void testFindByCarBrandOrType_WithType() {
        List<Car> cars = carRepository.findByCarBrandOrType(null, CarType.VAN.toString());
        assertEquals(1, cars.size());
        assertEquals(CarType.VAN, cars.get(0).getType());
    }

    @Test
    public void testFindByCarBrandOrType_WithBrandAndType() {
        List<Car> cars = carRepository.findByCarBrandOrType("Honda", CarType.VAN.toString());
        assertEquals(1, cars.size());
        assertEquals("Honda", cars.get(0).getCarBrand());
        assertEquals(CarType.VAN, cars.get(0).getType());
    }

    @Test
    public void testFindByCarBrandOrType_WithNoMatchingResults() {
        List<Car> cars = carRepository.findByCarBrandOrType("Ford", null);
        assertTrue(cars.isEmpty());
    }

    @Test
    public void testFindByTypeAndCarBrand() {
        Optional<Car> carOptional = carRepository.findByTypeAndCarBrand(CarType.SPORT, "Toyota");
        assertTrue(carOptional.isPresent());
        assertEquals("Toyota", carOptional.get().getCarBrand());
        assertEquals(CarType.SPORT, carOptional.get().getType());
    }

}