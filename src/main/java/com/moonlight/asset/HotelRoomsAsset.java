package com.moonlight.asset;


import com.moonlight.model.hotel.Amenity;
import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.model.enums.RoomBedType;
import com.moonlight.model.enums.RoomType;
import com.moonlight.model.enums.RoomView;
import com.moonlight.repository.hotel.AmenityRepository;
import com.moonlight.repository.hotel.HotelRoomRepository;
import com.moonlight.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Order(4)
public class HotelRoomsAsset implements CommandLineRunner {

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private CsvService csvService;

    @Override
    public void run(String... args) throws Exception {
        Set<Amenity> amenities = new HashSet<>(amenityRepository.findAll());
        List<String[]> rooms = csvService.readRoomsFromCsv("assetDocs/rooms.csv");

        saveHotelRooms(rooms, amenities);
    }

    private void saveHotelRooms(List<String[]> rooms, Set<Amenity> amenities) {
        for (String[] roomData : rooms) {
            Long roomNumber = Long.parseLong(roomData[0]);
            RoomType roomType = RoomType.valueOf(roomData[1].toUpperCase());
            RoomView roomView = RoomView.valueOf(roomData[2].toUpperCase());
            RoomBedType bedType = RoomBedType.valueOf(roomData[3].toUpperCase());

            HotelRoom newRoom = new HotelRoom();
            newRoom.setRoomNumber(roomNumber);
            newRoom.setRoomType(roomType);
            newRoom.setRoomView(roomView);
            newRoom.setBedType(bedType);
            newRoom.setAmenities(amenities);

            if (hotelRoomRepository.findByRoomNumber(newRoom.getRoomNumber()).isEmpty()) {
                hotelRoomRepository.save(newRoom);
            }
        }
    }
}
