package com.moonlight.service.impl.hotel;

import com.moonlight.advice.exception.RoomNotAvailableException;
import com.moonlight.model.enums.RoomType;
import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.model.hotel.HotelRoomReservation;
import com.moonlight.model.user.User;
import com.moonlight.repository.hotel.HotelRoomRepository;
import com.moonlight.repository.hotel.HotelRoomReservationRepository;
import com.moonlight.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelRoomReservationServiceImplTest {

    @Mock
    private HotelRoomRepository hotelRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HotelRoomReservationRepository hotelRoomReservationRepository;

    @InjectMocks
    private HotelRoomReservationServiceImpl hotelRoomReservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void checkRoomAvailability_roomAvailable() {
        HotelRoom hotelRoom = new HotelRoom();
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 9, 6);

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(Collections.emptyList());

        boolean result =
                hotelRoomReservationService.CheckRoomAvailability(hotelRoom, startDate, endDate);

        assertTrue(result, "Room shall be available, when no overlapping reservations exist");
    }

    @Test
    void checkRoomAvailability_roomNotAvailable() {
        HotelRoom hotelRoom = new HotelRoom();
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 9, 6);

        HotelRoomReservation existingReservation = new HotelRoomReservation();
        existingReservation.setHotelRoom(hotelRoom);
        existingReservation.setStartDate(LocalDate.of(2024, 8, 30));
        existingReservation.setEndDate(LocalDate.of(2024, 9, 4));

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(existingReservation));

        boolean result =
                hotelRoomReservationService.CheckRoomAvailability(hotelRoom, startDate, endDate);

        assertFalse(result, "Room shall not be available, when overlapping reservations exist");
    }

    @Test
    void checkRoomAvailability_EdgeReservationEndBeforeStart() {
        HotelRoom hotelRoom = new HotelRoom();
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 9, 6);

        HotelRoomReservation existingReservation = new HotelRoomReservation();
        existingReservation.setHotelRoom(hotelRoom);
        existingReservation.setStartDate(LocalDate.of(2024, 8, 30));
        existingReservation.setEndDate(LocalDate.of(2024, 8, 31));

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(existingReservation));

        boolean result =
                hotelRoomReservationService.CheckRoomAvailability(hotelRoom, startDate, endDate);

        assertTrue(result, "Room shall be available, when a previous reservation ends exactly before new one starts");
    }

    @Test
    void checkRoomAvailability_EdgeReservationStartAfterEnd() {
        HotelRoom hotelRoom = new HotelRoom();
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 9, 6);

        HotelRoomReservation existingReservation = new HotelRoomReservation();
        existingReservation.setHotelRoom(hotelRoom);
        existingReservation.setStartDate(LocalDate.of(2024, 8, 30));
        existingReservation.setEndDate(LocalDate.of(2024, 9, 1));

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(existingReservation));

        boolean result =
                hotelRoomReservationService.CheckRoomAvailability(hotelRoom, startDate, endDate);

        assertTrue(result, "Room shall be available, when a new reservation start exactly when the previous ends");
    }

    @Test
    void checkRoomAvailability_FullOverlappingReservation() {
        HotelRoom hotelRoom = new HotelRoom();
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 9, 6);

        HotelRoomReservation existingReservation = new HotelRoomReservation();
        existingReservation.setHotelRoom(hotelRoom);
        existingReservation.setStartDate(LocalDate.of(2024, 9, 1));
        existingReservation.setEndDate(LocalDate.of(2024, 9, 6));

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(existingReservation));

        boolean result =
                hotelRoomReservationService.CheckRoomAvailability(hotelRoom, startDate, endDate);

        assertFalse(result, "Room shall not be available, when reservations fully overlap");
    }

    @Test
    void makeReservation_success() {
        Long userId = 1L;
        Long hotelRoomId = 1L;
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 9, 6);
        int guestsAdult = 2;
        int guestsChildren = 0;

        User user = new User();
        user.setId(userId);

        HotelRoom hotelRoom = new HotelRoom();
        hotelRoom.setId(hotelRoomId);
        hotelRoom.setRoomType(RoomType.STANDARD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(hotelRoomRepository.findById(hotelRoomId)).thenReturn(Optional.of(hotelRoom));

        when(hotelRoomReservationRepository.save(any(HotelRoomReservation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        HotelRoomReservation reservation = hotelRoomReservationService
                .makeReservation(userId, hotelRoomId, startDate, endDate, guestsAdult, guestsChildren);

        assertNotNull(reservation);
        assertEquals(user, reservation.getUser());
        assertEquals(hotelRoom, reservation.getHotelRoom());
        assertEquals(startDate, reservation.getStartDate());
        assertEquals(endDate, reservation.getEndDate());

        verify(hotelRoomReservationRepository, times(1)).save(reservation);
    }

    @Test
    void makeReservation_userNotFound() {
        Long userId = 1L;
        Long hotelRoomId = 1L;
        int guestsAdult = 2;
        int guestsChildren = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, () -> {
            hotelRoomReservationService
                    .makeReservation(userId, hotelRoomId, LocalDate.now(), LocalDate.now().plusDays(5)
                    , guestsAdult, guestsChildren);
        });
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void makeReservation_hotelRoomNotFound() {
        Long userId = 1L;
        Long hotelRoomId = 1L;
        int guestsAdult = 2;
        int guestsChildren = 0;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(hotelRoomRepository.findById(hotelRoomId)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, () -> {
            hotelRoomReservationService
                    .makeReservation(userId, hotelRoomId, LocalDate.now(), LocalDate.now().plusDays(5)
                    , guestsAdult, guestsChildren);
        });
        verify(hotelRoomRepository, times(1)).findById(hotelRoomId);
    }

    @Test
    void makeReservation_roomNotAvailable() {
        Long userId = 1L;
        Long hotelRoomId = 1L;
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 9, 6);
        int guestsAdult = 2;
        int guestsChildren = 0;

        User user = new User();
        user.setId(userId);

        HotelRoom hotelRoom = new HotelRoom();
        hotelRoom.setId(hotelRoomId);
        hotelRoom.setRoomType(RoomType.STANDARD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(hotelRoomRepository.findById(hotelRoomId)).thenReturn(Optional.of(hotelRoom));

        HotelRoomReservation reservation = new HotelRoomReservation();
        reservation.setUser(user);
        reservation.setHotelRoom(hotelRoom);
        reservation.setStartDate(startDate.minusDays(2));
        reservation.setEndDate(endDate.plusDays(2));

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(reservation));


        assertThrows(RoomNotAvailableException.class, () -> {
            hotelRoomReservationService
                    .makeReservation(userId, hotelRoomId, startDate, endDate, guestsAdult, guestsChildren);
        });

        verify(hotelRoomRepository, times(1)).findById(hotelRoomId);
    }

    @Test
    void makeReservation_endDateBeforeStartDate() {
        Long userId = 1L;
        Long hotelRoomId = 1L;
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 8, 30);
        int guestsAdult = 2;
        int guestsChildren = 0;

        User user = new User();
        user.setId(userId);

        HotelRoom hotelRoom = new HotelRoom();
        hotelRoom.setId(hotelRoomId);
        hotelRoom.setRoomType(RoomType.STANDARD);

        when(hotelRoomRepository.findById(hotelRoomId)).thenReturn(Optional.of(hotelRoom));

        assertThrows(RuntimeException.class, () -> hotelRoomReservationService
                .makeReservation(userId, hotelRoomId, startDate, endDate,  guestsAdult,  guestsChildren));
        verify(hotelRoomReservationRepository, never()).save(any(HotelRoomReservation.class));
    }

    @Test
    void makeReservation_endDateEqualsStartDateSuccess() {
        Long userId = 1L;
        Long hotelRoomId = 1L;
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 9, 1);
        int guestsAdult = 2;
        int guestsChildren = 0;

        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        HotelRoom hotelRoom = new HotelRoom();
        hotelRoom.setId(hotelRoomId);
        hotelRoom.setRoomType(RoomType.STANDARD);

        when(hotelRoomRepository.findById(hotelRoomId)).thenReturn(Optional.of(hotelRoom));

        when(hotelRoomReservationRepository.save(any(HotelRoomReservation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        HotelRoomReservation reservation = hotelRoomReservationService
                .makeReservation(userId, hotelRoomId, startDate, endDate, guestsAdult, guestsChildren);


        assertNotNull(reservation);
        assertEquals(userId, reservation.getUser().getId());
        assertEquals(hotelRoomId, reservation.getHotelRoom().getId());
        assertEquals(startDate, reservation.getStartDate());
        assertEquals(endDate, reservation.getEndDate());

        verify(hotelRoomReservationRepository).save(any(HotelRoomReservation.class));
    }

    @Test
    void makeReservation_endDateAfterStartDateSuccess() {
        Long userId = 1L;
        Long hotelRoomId = 1L;
        LocalDate startDate = LocalDate.of(2024, 9, 1);
        LocalDate endDate = LocalDate.of(2024, 9, 10);
        int guestsAdult = 2;
        int guestsChildren = 0;

        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        HotelRoom hotelRoom = new HotelRoom();
        hotelRoom.setId(hotelRoomId);
        hotelRoom.setRoomType(RoomType.STANDARD);
        when(hotelRoomRepository.findById(hotelRoomId)).thenReturn(Optional.of(hotelRoom));

        when(hotelRoomReservationRepository.save(any(HotelRoomReservation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        HotelRoomReservation reservation = hotelRoomReservationService
                .makeReservation(userId, hotelRoomId, startDate, endDate, guestsAdult, guestsChildren);


        assertNotNull(reservation);
        assertEquals(userId, reservation.getUser().getId());
        assertEquals(hotelRoomId, reservation.getHotelRoom().getId());
        assertEquals(startDate, reservation.getStartDate());
        assertEquals(endDate, reservation.getEndDate());

        verify(hotelRoomReservationRepository).save(any(HotelRoomReservation.class));
    }
    @Test
    void makeReservation_nullStartDateShouldFail(){
        Long userId = 1L;
        Long hotelRoomId = 1L;
        LocalDate endDate = LocalDate.of(2024, 9, 10);
        int guestsAdult = 2;
        int guestsChildren = 0;

        assertThrows(RuntimeException.class, ()-> {
            hotelRoomReservationService.makeReservation(userId, hotelRoomId, null, endDate, guestsAdult, guestsChildren);
        });
    }

    @Test
    void makeReservation_nullEndDateShouldFail(){
        Long userId = 1L;
        Long hotelRoomId = 1L;
        LocalDate startDate = LocalDate.of(2024, 9, 10);
        int guestsAdult = 2;
        int guestsChildren = 0;

        assertThrows(RuntimeException.class, ()-> {
            hotelRoomReservationService.makeReservation(userId, hotelRoomId, startDate, null, guestsAdult, guestsChildren);
        });
    }

}