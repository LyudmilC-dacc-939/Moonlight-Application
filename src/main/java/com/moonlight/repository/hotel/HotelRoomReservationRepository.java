package com.moonlight.repository.hotel;

import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.model.hotel.HotelRoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HotelRoomReservationRepository extends JpaRepository<HotelRoomReservation, Long> {
    List<HotelRoomReservation> findByHotelRoom(HotelRoom hotelRoom);

    @Query(value = "SELECT * FROM room_reservations r WHERE " +
            ":userId IS NULL OR r.user_id = :userId ORDER BY " +
            "r.start_date ASC", nativeQuery = true)
    List<HotelRoomReservation> findByUserIdOrderByStartDate(@Param("userId") Long userId);

    List<HotelRoomReservation> findByUserId(Long userId);
}