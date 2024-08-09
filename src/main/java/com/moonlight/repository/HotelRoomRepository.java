package com.moonlight.repository;

import com.moonlight.model.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
    Optional<HotelRoom>findByRoomNumber(Long roomNumber);
}
