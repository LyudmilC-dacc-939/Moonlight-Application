package com.moonlight.service.impl;

import com.moonlight.model.Car;
import com.moonlight.repository.CarRepository;
import com.moonlight.service.CarService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<Car> findByQuerySearch(String carType, String carBrand) {
        return carRepository.findByCarBrandOrType(carType, carBrand);
    }
}
