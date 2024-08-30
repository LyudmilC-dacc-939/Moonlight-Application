package com.moonlight.repository.hotel;

import com.moonlight.model.enums.RoomType;
import com.moonlight.model.enums.RoomView;
import com.moonlight.model.hotel.Amenity;
import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.model.hotel.HotelRoomReservation;
import com.moonlight.model.user.User;
import com.moonlight.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class HotelRoomReservationRepositoryTest {

    @Autowired
    private HotelRoomReservationRepository hotelRoomReservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    private User user;
    private HotelRoom hotelRoom;

    @BeforeEach
    void setUp() {
        // Set up User and HotelRoom entities for testing
        user = new User();
        user.setFirstName("Test ");
        user.setLastName("User");
        user.setEmailAddress("testov.email@abv.bg");
        user.setPassword("pAr0la_za_T3s7");
        user.setPhoneNumber("008553285493");
        userRepository.save(user);

        hotelRoom = new HotelRoom();
        hotelRoom.setRoomNumber(105L);
        hotelRoom.setRoomType(RoomType.STANDARD);
        hotelRoom.setRoomView(RoomView.GARDEN);

        Amenity amenity = new Amenity();
        amenity.setAmenity("Couch");
        amenityRepository.save(amenity);
        Amenity amenity2 = new Amenity();
        amenity2.setAmenity("Jacuzzi");
        amenityRepository.save(amenity2);

        Set<Amenity> amenities = Set.of(amenity, amenity2);
        hotelRoom.setAmenities(amenities);
        Set<HotelRoom> hotelRooms = Set.of(hotelRoom);
        amenity.setHotelRooms(hotelRooms);
        amenity2.setHotelRooms(hotelRooms);
        hotelRoom = hotelRoomRepository.save(hotelRoom);


    }

    @Test
    public void testFindByHotelRoom() {
        HotelRoomReservation reservation = createReservation();
        hotelRoomReservationRepository.save(reservation);

        List<HotelRoomReservation> reservations = hotelRoomReservationRepository.findByHotelRoom(hotelRoom);
        assertThat(reservations).isNotEmpty();
        assertThat(reservations).contains(reservation);
    }

    @Test
    public void testFindByUserIdOrderByStartDate() {
        HotelRoomReservation reservation1 = createReservation();
        reservation1.setUser(user);
        reservation1.setHotelRoom(hotelRoom);
        reservation1.setStartDate(LocalDate.of(2023, 8, 1));
        reservation1.setEndDate(LocalDate.of(2023, 8, 14));
        hotelRoomReservationRepository.save(reservation1);

        HotelRoomReservation reservation2 = createReservation();
        reservation2.setUser(user);
        reservation2.setHotelRoom(hotelRoom);
        reservation2.setStartDate(LocalDate.of(2023, 7, 1));
        reservation2.setEndDate(LocalDate.of(2023, 7, 10));
        hotelRoomReservationRepository.save(reservation2);

        List<HotelRoomReservation> reservations = hotelRoomReservationRepository.findByUserIdOrderByStartDate(user.getId());
        assertThat(reservations).hasSize(2);
        assertThat(reservations.get(0).getStartDate()).isEqualTo(LocalDate.of(2023, 7, 1));
        assertThat(reservations.get(1).getStartDate()).isEqualTo(LocalDate.of(2023, 8, 1));
    }

    @Test
    public void testFindByUserIdOrderByStartDate_NullUserId() {
        HotelRoomReservation reservation1 = createReservation();
        reservation1.setUser(user);
        reservation1.setHotelRoom(hotelRoom);
        reservation1.setStartDate(LocalDate.of(2023, 8, 1));
        reservation1.setEndDate(LocalDate.of(2023, 8, 14));
        hotelRoomReservationRepository.save(reservation1);

        List<HotelRoomReservation> reservations = hotelRoomReservationRepository.findByUserIdOrderByStartDate(null);
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0)).isEqualTo(reservation1);
    }

    @Test
    public void testFindByUserId() {
        HotelRoomReservation reservation = createReservation();
        hotelRoomReservationRepository.save(reservation);

        List<HotelRoomReservation> reservations = hotelRoomReservationRepository.findByUserId(user.getId());
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0)).isEqualTo(reservation);
    }

    private HotelRoomReservation createReservation() {
        HotelRoomReservation reservation = new HotelRoomReservation();
        reservation.setUser(user);
        reservation.setHotelRoom(hotelRoom);
        reservation.setStartDate(LocalDate.now());
        reservation.setEndDate(LocalDate.now().plusDays(3));
        reservation.setDuration(3);
        reservation.setTotalCost(300.0);
        reservation.setGuestsAdult(2);
        reservation.setGuestsChildren(1);
        return reservation;
    }
}