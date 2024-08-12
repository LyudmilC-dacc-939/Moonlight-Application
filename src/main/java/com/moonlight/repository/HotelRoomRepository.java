package com.moonlight.repository;

import com.moonlight.model.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
    Optional<HotelRoom> findByRoomNumber(Long roomNumber);

    @Query(value = "SELECT * FROM hotel_rooms hr WHERE " +
            "(:roomType IS NULL OR hr.room_type LIKE %:roomType%) AND " +
            "(:roomView IS NULL OR hr.room_view LIKE %:roomView%) AND " +
            "(:roomBedType IS NULL OR hr.room_bed_type LIKE %:roomBedType%)", nativeQuery = true)
    Set<HotelRoom> findByRoomTypeOrViewTypeOrBedType(@Param("roomType") String roomType,
                                                     @Param("roomView") String roomView,
                                                     @Param("roomBedType") String roomBedType);

}
