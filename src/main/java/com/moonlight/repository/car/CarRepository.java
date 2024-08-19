package com.moonlight.repository.car;

import com.moonlight.model.car.Car;
import com.moonlight.model.enums.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query(value = "SELECT * FROM cars c WHERE " +
            "(:carBrand IS NULL OR c.car_brand LIKE %:carBrand%) AND " +
            "(:carType IS NULL OR c.car_type = :carType)", nativeQuery = true)
    List<Car> findByCarBrandOrType(@Param("carBrand") String carBrand,
                                   @Param("carType") String carType);

    Optional<Car> findByTypeAndCarBrand(CarType type, String carBrand);
}

