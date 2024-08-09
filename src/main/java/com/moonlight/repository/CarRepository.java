package com.moonlight.repository;

import com.moonlight.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
    // add custom query methods here if needed
}

