package com.moonlight.repository.hotel;

import com.moonlight.model.hotel.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    Optional<Amenity> findByAmenity(String amenity);
}
