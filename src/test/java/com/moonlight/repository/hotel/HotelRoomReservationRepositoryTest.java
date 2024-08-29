package com.moonlight.repository.hotel;

import com.moonlight.model.enums.RoomType;
import com.moonlight.model.enums.RoomView;
import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.model.hotel.HotelRoomReservation;
import com.moonlight.model.user.User;
import com.moonlight.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HotelRoomReservationRepositoryTest {

    @Autowired
    private HotelRoomReservationRepository hotelRoomReservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    private User user;
    private HotelRoom hotelRoom;

    @BeforeEach
    void setUp() {
        // Set up User and HotelRoom entities for testing
        user = new User();
        user.setFirstName("Test ");
        user.setLastName("User");
        user = userRepository.save(user);

        hotelRoom = new HotelRoom();
        hotelRoom.setRoomNumber(105L);
        hotelRoom.setRoomType(RoomType.STANDARD);
        hotelRoom.setRoomView(RoomView.GARDEN);
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
        reservation1.setStartDate(LocalDate.of(2023, 8, 1));
        hotelRoomReservationRepository.save(reservation1);

        HotelRoomReservation reservation2 = createReservation();
        reservation2.setStartDate(LocalDate.of(2023, 7, 1));
        hotelRoomReservationRepository.save(reservation2);

        List<HotelRoomReservation> reservations = hotelRoomReservationRepository.findByUserIdOrderByStartDate(user.getId());
        assertThat(reservations).hasSize(2);
        assertThat(reservations.get(0).getStartDate()).isEqualTo(LocalDate.of(2023, 7, 1));
        assertThat(reservations.get(1).getStartDate()).isEqualTo(LocalDate.of(2023, 8, 1));
    }

    @Test
    public void testFindByUserIdOrderByStartDate_NullUserId() {
        HotelRoomReservation reservation = createReservation();
        hotelRoomReservationRepository.save(reservation);

        List<HotelRoomReservation> reservations = hotelRoomReservationRepository.findByUserIdOrderByStartDate(null);
        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0)).isEqualTo(reservation);
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