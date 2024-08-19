package com.moonlight.service;

import com.moonlight.model.car.Car;

import java.util.List;

public interface CarService {

    List<Car> findByQuerySearch(String carType, String carBrand);
}
