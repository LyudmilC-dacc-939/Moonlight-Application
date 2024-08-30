package com.moonlight.service.impl.hotel;

import com.moonlight.advice.exception.InvalidDateRangeException;
import com.moonlight.advice.exception.RoomNotAvailableException;
import com.moonlight.dto.hotel.HotelRoomAvailabilityResponse;
import com.moonlight.model.enums.RoomBedType;
import com.moonlight.model.enums.RoomType;
import com.moonlight.model.enums.RoomView;
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
import java.util.Arrays;
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
    private Long userId;
    private Long roomNumber;
    private User user;
    private HotelRoom hotelRoom;
    private Integer guestsAdult;
    private Integer guestsChildren;
    private LocalDate startDate;
    private LocalDate endDate;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        roomNumber = 1L;

        user = new User();
        user.setId(userId);

        hotelRoom = new HotelRoom();
        hotelRoom.setRoomNumber(roomNumber);
        hotelRoom.setRoomType(RoomType.STANDARD);

        guestsAdult = 2;
        guestsChildren = 0;

        startDate = LocalDate.now().plusDays(1);
        endDate = LocalDate.now().plusDays(10);
    }


    @Test
    void checkRoomAvailability_roomAvailable() {
        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(Collections.emptyList());

        boolean result =
                hotelRoomReservationService.checkRoomAvailability(hotelRoom, startDate, endDate);

        assertTrue(result, "Room shall be available, when no overlapping reservations exist");
    }

    @Test
    void checkRoomAvailability_roomNotAvailable() {
        HotelRoomReservation existingReservation = new HotelRoomReservation();
        existingReservation.setHotelRoom(hotelRoom);
        existingReservation.setStartDate(startDate.minusDays(2));
        existingReservation.setEndDate(endDate.minusDays(2));

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(existingReservation));

        boolean result =
                hotelRoomReservationService.checkRoomAvailability(hotelRoom, startDate, endDate);

        assertFalse(result, "Room shall not be available, when overlapping reservations exist");
    }

    @Test
    void checkRoomAvailability_roomNotAvailableIfBookedForOneDayDueToOverlapping() {
        HotelRoomReservation existingReservation = new HotelRoomReservation();
        existingReservation.setHotelRoom(hotelRoom);
        existingReservation.setStartDate(LocalDate.now().plusDays(1));
        existingReservation.setEndDate(LocalDate.now().plusDays(1));

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(existingReservation));

        boolean result =
                hotelRoomReservationService.checkRoomAvailability(hotelRoom, startDate, endDate);

        assertFalse(result, "Room shall not be available, when overlapping reservations exist");
    }

    @Test
    void checkRoomAvailability_EdgeReservationEndBeforeStart() {
        HotelRoomReservation existingReservation = new HotelRoomReservation();
        existingReservation.setHotelRoom(hotelRoom);
        existingReservation.setStartDate(startDate.minusDays(10));
        existingReservation.setEndDate(startDate.minusDays(1));

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(existingReservation));

        boolean result =
                hotelRoomReservationService.checkRoomAvailability(hotelRoom, startDate, endDate);

        assertTrue(result, "Room shall be available, when a previous reservation ends exactly before new one starts");
    }

    @Test
    void checkRoomAvailability_EdgeReservationStartAfterEnd() {
        HotelRoomReservation existingReservation = new HotelRoomReservation();
        existingReservation.setHotelRoom(hotelRoom);
        existingReservation.setStartDate(startDate.minusDays(10));
        existingReservation.setEndDate(startDate);

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(existingReservation));

        boolean result =
                hotelRoomReservationService.checkRoomAvailability(hotelRoom, startDate, endDate);

        assertTrue(result, "Room shall be available, when a new reservation start exactly when the previous ends");
    }

    @Test
    void checkRoomAvailability_FullOverlappingReservation() {
        HotelRoomReservation existingReservation = new HotelRoomReservation();
        existingReservation.setHotelRoom(hotelRoom);
        existingReservation.setStartDate(startDate);
        existingReservation.setEndDate(endDate);

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(existingReservation));

        boolean result =
                hotelRoomReservationService.checkRoomAvailability(hotelRoom, startDate, endDate);

        assertFalse(result, "Room shall not be available, when reservations fully overlap");
    }

    @Test
    void makeReservation_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(hotelRoomRepository.findByRoomNumber(roomNumber)).thenReturn(Optional.of(hotelRoom));

        when(hotelRoomReservationRepository.save(any(HotelRoomReservation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        HotelRoomReservation reservation = hotelRoomReservationService
                .makeReservation(userId, roomNumber, startDate, endDate, guestsAdult, guestsChildren);

        assertNotNull(reservation);
        assertEquals(user, reservation.getUser());
        assertEquals(hotelRoom, reservation.getHotelRoom());
        assertEquals(startDate, reservation.getStartDate());
        assertEquals(endDate, reservation.getEndDate());

        verify(hotelRoomReservationRepository, times(1)).save(reservation);
    }

    @Test
    void makeReservation_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            hotelRoomReservationService
                    .makeReservation(userId, roomNumber, LocalDate.now(), LocalDate.now().plusDays(5)
                            , guestsAdult, guestsChildren);
        });
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void makeReservation_hotelRoomNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(hotelRoomRepository.findByRoomNumber(roomNumber)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            hotelRoomReservationService
                    .makeReservation(userId, roomNumber, LocalDate.now(), LocalDate.now().plusDays(5)
                            , guestsAdult, guestsChildren);
        });
        verify(hotelRoomRepository, times(1)).findByRoomNumber(roomNumber);
    }

    @Test
    void makeReservation_roomNotAvailable() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(hotelRoomRepository.findByRoomNumber(roomNumber)).thenReturn(Optional.of(hotelRoom));

        HotelRoomReservation reservation = new HotelRoomReservation();
        reservation.setUser(user);
        reservation.setHotelRoom(hotelRoom);
        reservation.setStartDate(startDate.minusDays(2));
        reservation.setEndDate(endDate.plusDays(2));

        when(hotelRoomReservationRepository.findByHotelRoom(hotelRoom)).thenReturn(List.of(reservation));

        assertThrows(RoomNotAvailableException.class, () -> {
            hotelRoomReservationService
                    .makeReservation(userId, roomNumber, startDate, endDate, guestsAdult, guestsChildren);
        });

        verify(hotelRoomRepository, times(1)).findByRoomNumber(roomNumber);
    }

    @Test
    void makeReservation_endDateBeforeStartDate() {
        LocalDate endDate = startDate.minusDays(2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(hotelRoomRepository.findByRoomNumber(roomNumber)).thenReturn(Optional.of(hotelRoom));

        assertThrows(RuntimeException.class, () -> hotelRoomReservationService
                .makeReservation(userId, roomNumber, startDate, endDate, guestsAdult, guestsChildren));
        verify(hotelRoomReservationRepository, never()).save(any(HotelRoomReservation.class));
    }

    @Test
    void makeReservation_endDateEqualsStartDateSuccess() {
        LocalDate endDate = startDate;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(hotelRoomRepository.findByRoomNumber(roomNumber)).thenReturn(Optional.of(hotelRoom));

        when(hotelRoomReservationRepository.save(any(HotelRoomReservation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        HotelRoomReservation reservation = hotelRoomReservationService
                .makeReservation(userId, roomNumber, startDate, endDate, guestsAdult, guestsChildren);

        assertNotNull(reservation);
        assertEquals(userId, reservation.getUser().getId());
        assertEquals(roomNumber, reservation.getHotelRoom().getRoomNumber());
        assertEquals(startDate, reservation.getStartDate());
        assertEquals(endDate, reservation.getEndDate());

        verify(hotelRoomReservationRepository).save(any(HotelRoomReservation.class));
    }

    @Test
    void makeReservation_endDateAfterStartDateSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(hotelRoomRepository.findByRoomNumber(roomNumber)).thenReturn(Optional.of(hotelRoom));

        when(hotelRoomReservationRepository.save(any(HotelRoomReservation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        HotelRoomReservation reservation = hotelRoomReservationService
                .makeReservation(userId, roomNumber, startDate, endDate, guestsAdult, guestsChildren);

        assertNotNull(reservation);
        assertEquals(userId, reservation.getUser().getId());
        assertEquals(roomNumber, reservation.getHotelRoom().getRoomNumber());
        assertEquals(startDate, reservation.getStartDate());
        assertEquals(endDate, reservation.getEndDate());

        verify(hotelRoomReservationRepository).save(any(HotelRoomReservation.class));
    }

    @Test
    void makeReservation_nullStartDateShouldFail() {
        assertThrows(RuntimeException.class, () -> {
            hotelRoomReservationService.makeReservation(userId, roomNumber, null, endDate, guestsAdult, guestsChildren);
        });
    }

    @Test
    void makeReservation_nullEndDateShouldFail() {
        assertThrows(RuntimeException.class, () -> {
            hotelRoomReservationService.makeReservation(userId, roomNumber, startDate, null, guestsAdult, guestsChildren);
        });
    }

    @Test
    void makeReservation_guestsNumberExceedRoomCapacity() {
        int guestsAdult = 6;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(hotelRoomRepository.findByRoomNumber(roomNumber)).thenReturn(Optional.of(hotelRoom));

        RoomNotAvailableException thrown = assertThrows(RoomNotAvailableException.class, () ->
                hotelRoomReservationService.makeReservation(userId, roomNumber, startDate, endDate
                        , guestsAdult, guestsChildren));

        assertEquals("Total number of guests exceeds maximum allowed guest for the room type.", thrown.getMessage());
        verify(hotelRoomReservationRepository, never()).save(any(HotelRoomReservation.class));
    }

    @Test
    void shouldThrowInvalidDateRangeExceptionWhenEndDateIsBeforeStartDate() {
        LocalDate start = startDate.plusDays(10);
        LocalDate end = endDate.minusDays(5);

        InvalidDateRangeException exception = assertThrows(InvalidDateRangeException.class, () ->
                hotelRoomReservationService.getAvailableRooms(start, end));

        assertEquals("End date cannot be before start date", exception.getMessage());
    }

    @Test
    void returnEmptyListWhenNoRoomAvailable() {
        when(hotelRoomRepository.findAll()).thenReturn(Collections.emptyList());
        List<HotelRoomAvailabilityResponse> availableRooms =
                hotelRoomReservationService.getAvailableRooms(startDate, endDate);
        assertTrue(availableRooms.isEmpty());
    }
    @Test
    void convertHotelRoomToAvailabilityResponse() {
        HotelRoom room = new HotelRoom();
        room.setRoomNumber(101L);
        room.setRoomType(RoomType.STANDARD);
        room.setRoomView(RoomView.SEA);
        room.setBedType(RoomBedType.SINGLE_BED);

        HotelRoomReservationServiceImpl service = new HotelRoomReservationServiceImpl();
        HotelRoomAvailabilityResponse response = service.convertToAvailableHotelRoomResponse(room);

        assertEquals(101L, response.getRoomNumber());
        assertEquals("STANDARD", response.getRoomType());
        assertEquals("SEA", response.getRoomView());
        assertEquals("SINGLE_BED", response.getRoomBedType());
        assertEquals(220, response.getRoomPricePerNight());
        assertEquals(2, response.getMaxNumberOfGuests());
    }
}