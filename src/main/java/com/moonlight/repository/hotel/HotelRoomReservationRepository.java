package com.moonlight.repository.hotel;

import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.model.hotel.HotelRoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HotelRoomReservationRepository extends JpaRepository <HotelRoomReservation, Long> {
    List<HotelRoomReservation> findByHotelRoom(HotelRoom hotelRoom);
}
