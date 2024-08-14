package com.moonlight.repository;

import com.moonlight.model.Car;
import com.moonlight.model.enums.CarType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    // add custom query methods here if needed
    Optional<Car> findByTypeAndCarBrand (CarType type, String carBrand);
}

