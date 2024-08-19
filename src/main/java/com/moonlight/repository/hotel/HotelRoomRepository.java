package com.moonlight.repository.hotel;

import com.moonlight.model.hotel.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
    Optional<HotelRoom> findByRoomNumber(Long roomNumber);


    @Query(value = "SELECT * FROM rooms r WHERE " +
            "(:roomNumber IS NULL OR r.room_number = :roomNumber) AND " +
            "(:roomType IS NULL OR r.room_type = :roomType) AND " +
            "(:roomView IS NULL OR r.room_view = :roomView) AND " +
            "(:roomBedType IS NULL OR r.bed_type = :roomBedType)", nativeQuery = true)
    Set<HotelRoom> findByRoomNumberByRoomTypeOrViewTypeOrBedType(@Param("roomNumber") Long roomNumber,
                                                                 @Param("roomType") String roomType,
                                                                 @Param("roomView") String roomView,
                                                                 @Param("roomBedType") String roomBedType);
}