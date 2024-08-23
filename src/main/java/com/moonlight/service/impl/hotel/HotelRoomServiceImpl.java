package com.moonlight.service.impl.hotel;

import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.repository.hotel.HotelRoomRepository;
import com.moonlight.service.HotelRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class HotelRoomServiceImpl implements HotelRoomService {

    private final HotelRoomRepository hotelRoomRepository;

    @Autowired
    public HotelRoomServiceImpl(HotelRoomRepository hotelRoomRepository) {
        this.hotelRoomRepository = hotelRoomRepository;
    }

    @Override
    public Set<HotelRoom> findByRoomNumberByRoomTypeOrViewTypeOrBedType(
            Long roomNumber, String roomType, String roomView, String roomBedType) {
        return hotelRoomRepository.findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                roomNumber, roomType, roomBedType, roomBedType);
    }
}
