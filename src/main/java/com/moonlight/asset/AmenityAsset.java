package com.moonlight.asset;

import com.moonlight.model.hotel.Amenity;
import com.moonlight.repository.hotel.AmenityRepository;
import com.moonlight.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Order(3)
public class AmenityAsset implements CommandLineRunner {
    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private CsvService csvService;

    @Override
    public void run(String... args) throws Exception {
        List<String> amenities = csvService.readAmenityFromCsv("assetDocs/amenities.csv");

        for (String amenityName : amenities) {
            // Checks if Amenity already exists in the DB
            Optional<Amenity> existingAmenity = amenityRepository.findByAmenity(amenityName);
            Amenity amenity = new Amenity();
            if (existingAmenity.isEmpty()) {
                amenity.setAmenity(amenityName);
                amenityRepository.save(amenity);
            }
        }
    }
}
