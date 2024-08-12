package com.moonlight.service;

import com.moonlight.model.HotelRoom;

import java.util.Set;

public interface HotelRoomService {
    Set<HotelRoom> findByRoomTypeOrViewTypeOrBedType(String roomType,
                                                     String roomView,
                                                     String roomBedType);
}
