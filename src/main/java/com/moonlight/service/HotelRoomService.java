package com.moonlight.service;

import com.moonlight.model.hotel.HotelRoom;

import java.util.Set;

public interface HotelRoomService {
    Set<HotelRoom> findByRoomNumberByRoomTypeOrViewTypeOrBedType(Long roomNumber,
                                                                 String roomType,
                                                                 String roomView,
                                                                 String roomBedType);
}