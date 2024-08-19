package com.moonlight.asset;

import com.moonlight.model.car.Car;
import com.moonlight.model.car.FileResource;
import com.moonlight.model.enums.CarType;
import com.moonlight.repository.car.CarRepository;
import com.moonlight.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private void saveCars(List<String[]> carsFromCsv) throws Exception {
        List<Car> carsInDataBase = carRepository.findAll();
        Set<String> carsInCsvSet = new HashSet<>();
        // Save or update cars from the CSV

        for (String[] carData : carsFromCsv) {
            String carTypeStr = carData[0].trim().toUpperCase();

            CarType carType = CarType.valueOf(carTypeStr);
            String carBrand = carData[1];

            try {
                Car car = carRepository.findByTypeAndCarBrand(carType, carBrand).orElse(new Car());
                car.setType(carType);
                car.setCarBrand(carBrand);


                List<FileResource> fileResources = new ArrayList<>();
                for (int i = 2; i <= 4; i++) {
                    String imagePath = carData[i];
                    FileResource fileResource = createFileResource(imagePath);
                    fileResource.setCar(car);
                    fileResources.add(fileResource);
                }
                car.setFileResources(fileResources);

                if (carRepository.findByTypeAndCarBrand(car.getType(), car.getCarBrand()).isEmpty()) {
                    carRepository.save(car);
                }
            } catch (Exception e) {
                System.err.println("Error saving car: " + e.getMessage());
            }

            carsInCsvSet.add(carTypeStr + ":" + carBrand);
            // Combine type and brand as unique identifier
        }

        for (Car car : carsInDataBase) {
            String carIdentifier = car.getType().name() + ":" + car.getCarBrand();
            if (!carsInCsvSet.contains(carIdentifier)) {
                carRepository.delete(car);
                // Remove cars that are in the database but not in the CSV
            }
        }

    }

    private FileResource createFileResource(String imagePath) throws Exception {
        String filePath = "src/main/resources/assetDocs/images/" + imagePath;
        Path path = Paths.get(filePath);
        FileResource fileResource = null;
        byte[] imageData = Files.readAllBytes(path);
        fileResource = new FileResource();
        fileResource.setDataValue(imageData);

        return fileResource;
    }

}
