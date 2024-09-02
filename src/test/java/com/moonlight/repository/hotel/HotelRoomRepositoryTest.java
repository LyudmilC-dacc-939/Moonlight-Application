package com.moonlight.repository.hotel;

import com.moonlight.model.enums.RoomBedType;
import com.moonlight.model.enums.RoomType;
import com.moonlight.model.enums.RoomView;
import com.moonlight.model.hotel.Amenity;
import com.moonlight.model.hotel.HotelRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HotelRoomRepositoryTest {

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @BeforeEach
    public void setUp() {
        HotelRoom room1 = new HotelRoom();
        room1.setRoomNumber(101L);
        room1.setRoomType(RoomType.STANDARD);
        room1.setRoomView(RoomView.SEA);
        room1.setBedType(RoomBedType.SEPARATE_BEDS);

        HotelRoom room2 = new HotelRoom();
        room2.setRoomNumber(102L);
        room2.setRoomType(RoomType.STUDIO);
        room2.setRoomView(RoomView.GARDEN);
        room2.setBedType(RoomBedType.SINGLE_BED);

        Amenity amenity = new Amenity();
        amenity.setAmenity("Couch");
        amenityRepository.save(amenity);
        Amenity amenity2 = new Amenity();
        amenity2.setAmenity("Jacuzzi");
        amenityRepository.save(amenity2);
        Set<Amenity> amenities = Set.of(amenity, amenity2);
        Set<HotelRoom> hotelRooms = Set.of(room1, room2);
        room1.setAmenities(amenities);
        room2.setAmenities(amenities);
        amenity.setHotelRooms(hotelRooms);
        amenity2.setHotelRooms(hotelRooms);

        hotelRoomRepository.save(room1);
        hotelRoomRepository.save(room2);
    }

    @Test
    void findByRoomNumber() {
        Optional<HotelRoom> foundRoom = hotelRoomRepository.findByRoomNumber(101L);

        assertThat(foundRoom).isPresent();
        assertThat(foundRoom.get().getRoomNumber()).isEqualTo(101L);
        assertThat(foundRoom.get().getRoomType().name()).isEqualTo("STANDARD");
        assertThat(foundRoom.get().getRoomView().name()).isEqualTo("SEA");
    }

    @Test
    void findByRoomNumberByRoomTypeOrViewTypeOrBedType() {
        Set<HotelRoom> foundRooms = hotelRoomRepository.findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                101L, "Standard", null, null);

        assertEquals(1, foundRooms.size());
        assertEquals(101L, foundRooms.iterator().next().getRoomNumber());

        foundRooms = hotelRoomRepository.findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                null, "Studio", "Garden", null);

        assertEquals(1, foundRooms.size());
        assertEquals(102L, foundRooms.iterator().next().getRoomNumber());

        foundRooms = hotelRoomRepository.findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                null, null, null, null);

        assertEquals(2, foundRooms.size());
    }


    @Test
    public void testFindByRoomNumber_NotFound() {
        Optional<HotelRoom> foundRoom = hotelRoomRepository.findByRoomNumber(999L);

        assertThat(foundRoom).isNotPresent();
    }

    @Test
    public void testFindByRoomNumberByRoomTypeOrViewTypeOrBedType_NotFound() {
        Set<HotelRoom> foundRooms = hotelRoomRepository.findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                999L, "STUDIO", "SEA", "SINGLE_BED");

        assertTrue(foundRooms.isEmpty());
    }

    @Test
    public void testFindByRoomNumberByRoomTypeOrViewTypeOrBedType_BadRequest() {
        assertThrows(DataIntegrityViolationException.class, () ->
                hotelRoomRepository.findByRoomNumberByRoomTypeOrViewTypeOrBedType(
                        999L, "randomString", "RandomString", "RandomString"));

    }
}