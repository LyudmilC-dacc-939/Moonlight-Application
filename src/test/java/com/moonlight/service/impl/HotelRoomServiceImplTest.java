package com.moonlight.service.impl;

import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.model.enums.RoomBedType;
import com.moonlight.model.enums.RoomType;
import com.moonlight.model.enums.RoomView;
import com.moonlight.repository.hotel.HotelRoomRepository;
import com.moonlight.service.impl.hotel.HotelRoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelRoomServiceImplTest {

    @Mock
    private HotelRoomRepository hotelRoomRepository;

    @InjectMocks
    private HotelRoomServiceImpl hotelRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByRoomNumberByRoomTypeOrViewTypeOrBedType() {
        // Setup for the search
        Long roomNumber = 101L;
        String roomType = RoomType.STANDARD.toString(); // Convert enum to string
        String roomView = RoomView.SEA.toString(); // Convert enum to string
        String roomBedType = RoomBedType.SINGLE_BED.toString(); // Convert enum to string

        // Expected hotel room with enum values
        HotelRoom expectedHotelRoom = new HotelRoom();
        expectedHotelRoom.setRoomNumber(roomNumber);
        expectedHotelRoom.setRoomType(RoomType.STANDARD); // Enum value
        expectedHotelRoom.setRoomView(RoomView.SEA); // Enum value
        expectedHotelRoom.setBedType(RoomBedType.SINGLE_BED); // Enum value

        // Stubbing String values for the search
        when(hotelRoomRepository.findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                roomNumber, roomType, roomView, roomBedType))
                .thenReturn(Set.of(expectedHotelRoom));

        // "Finds" the existing room/s
        Set<HotelRoom> actualRooms = hotelRoomRepository.findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                roomNumber, roomType, roomView, roomBedType);

        // Argument Captor checks the enums have correctly been made into Strings
        ArgumentCaptor<Long> roomNumberCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> roomTypeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> roomViewCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> roomBedTypeCaptor = ArgumentCaptor.forClass(String.class);

        verify(hotelRoomRepository).findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                roomNumberCaptor.capture(), roomTypeCaptor.capture(), roomViewCaptor.capture(), roomBedTypeCaptor.capture());

        assertEquals(roomNumber, roomNumberCaptor.getValue());
        assertEquals(roomType, roomTypeCaptor.getValue());
        assertEquals(roomView, roomViewCaptor.getValue());
        assertEquals(roomBedType, roomBedTypeCaptor.getValue());

        // Assert final
        assertEquals(1, actualRooms.size());
        assertEquals(expectedHotelRoom, actualRooms.iterator().next());
    }

    @Test
    void findByRoomNumberByRoomTypeOrViewTypeOrBedTypeIfAllNull() {
        // Setup for the search
        Long roomNumber = null;
        String roomType = null;
        String roomView = null;
        String roomBedType = null;

        // Expected hotel room with enum values
        HotelRoom expectedHotelRoom = new HotelRoom();
        expectedHotelRoom.setRoomNumber(null);
        expectedHotelRoom.setRoomType(null); // Enum value
        expectedHotelRoom.setRoomView(null); // Enum value
        expectedHotelRoom.setBedType(null); // Enum value

        // Stubbing String values for the search
        when(hotelRoomRepository.findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                roomNumber, roomType, roomView, roomBedType))
                .thenReturn(Set.of(expectedHotelRoom));

        // "Finds" the existing room/s
        Set<HotelRoom> actualRooms = hotelRoomRepository.findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                roomNumber, roomType, roomView, roomBedType);

        // Argument Captor checks the enums have correctly been made into Strings
        ArgumentCaptor<Long> roomNumberCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> roomTypeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> roomViewCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> roomBedTypeCaptor = ArgumentCaptor.forClass(String.class);

        verify(hotelRoomRepository).findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                roomNumberCaptor.capture(), roomTypeCaptor.capture(), roomViewCaptor.capture(), roomBedTypeCaptor.capture());

        assertEquals(roomNumber, roomNumberCaptor.getValue());
        assertEquals(roomType, roomTypeCaptor.getValue());
        assertEquals(roomView, roomViewCaptor.getValue());
        assertEquals(roomBedType, roomBedTypeCaptor.getValue());

        // Assert final
        assertEquals(1, actualRooms.size());
        assertEquals(expectedHotelRoom, actualRooms.iterator().next());
    }
}