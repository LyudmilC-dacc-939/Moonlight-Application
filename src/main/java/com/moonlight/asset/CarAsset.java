package com.moonlight.asset;

import com.moonlight.model.Car;
import com.moonlight.model.enums.CarType;
import com.moonlight.repository.CarRepository;
import com.moonlight.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Order(5)
public class CarAsset implements CommandLineRunner {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CsvService csvService;

    @Override
    public void run(String... args) throws Exception {
        List<String[]> carsFromCsv = csvService.readCarsFromCsv("assetDocs/cars.csv");
        saveCars(carsFromCsv);
    }
    private void saveCars(List<String[]> carsFromCsv) {
        List<Car> carsInDataBase = carRepository.findAll();
        Set<String> carsInCsvSet = new HashSet<>();
        // Save or update cars from the CSV

        for (String[] carData : carsFromCsv) {
            String carTypeStr = carData[0].trim().toUpperCase();

            CarType carType = CarType.valueOf(carTypeStr);
            String carBrand = carData[1];

            Car newCar = new Car();
            newCar.setType(carType);
            newCar.setCarBrand(carBrand);

            carsInCsvSet.add(carTypeStr + ":" + carBrand);
            // Combine type and brand as unique identifier

            if (carRepository.findByTypeAndCarBrand(newCar.getType(), newCar.getCarBrand()).isEmpty()) {
                carRepository.save(newCar);
            }
        }
        for (Car car : carsInDataBase) {
            String carIdentifier = car.getType().name() + ":" + car.getCarBrand();
            if (!carsInCsvSet.contains(carIdentifier)) {
                carRepository.delete(car);
                // Remove cars that are in the database but not in the CSV
            }
        }
    }
}
