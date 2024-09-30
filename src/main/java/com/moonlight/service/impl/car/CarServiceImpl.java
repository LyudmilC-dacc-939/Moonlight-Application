package com.moonlight.service.impl.car;

import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.ImageResponse;
import com.moonlight.model.car.Car;
import com.moonlight.model.car.FileResource;
import com.moonlight.repository.car.CarRepository;
import com.moonlight.repository.car.FileResourceRepository;
import com.moonlight.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private FileResourceRepository fileResourceRepository;


    @Override
    public List<Car> findByQuerySearch(String carType, String carBrand) {

        return carRepository.findByCarBrandOrType(carType, carBrand);
    }

    public List<ImageResponse> getCarImages(Long carId, Long imageId) {
        List<FileResource> fileResources = new ArrayList<>();

        if (imageId != null) {
            if (carId != null) {
                // Validate if the imageId belongs to the carId
                List<FileResource> allImagesForCar = fileResourceRepository.findByCarId(carId);
                if (allImagesForCar.isEmpty()) {
                    throw new RecordNotFoundException("Car with ID " + carId + " does not exist.");
                }

                List<Long> validImageIds = allImagesForCar.stream()
                        .map(FileResource::getId)
                        .collect(Collectors.toList());

                if (!validImageIds.contains(imageId)) {
                    throw new UnavailableResourceException("Image ID " + imageId + " does not belong to Car ID " + carId + ".");
                }

                fileResources = fileResourceRepository.findById(imageId)
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList());

            } else {
                // Fetch the image by imageId only
                fileResources = fileResourceRepository.findById(imageId)
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList());
            }
        } else if (carId != null) {
            // Fetch all images for a specific carId
            fileResources = fileResourceRepository.findByCarId(carId);
            if (fileResources.isEmpty()) {
                throw new RecordNotFoundException("Car with ID " + carId + " does not exist.");
            }
        }

        return fileResources.stream()
                .map(fileResource -> new ImageResponse(
                        fileResource.getId(),
                        fileResource.getCar().getId(),
                        Base64.getEncoder().encodeToString(fileResource.getDataValue())))
                .collect(Collectors.toList());
    }
}
