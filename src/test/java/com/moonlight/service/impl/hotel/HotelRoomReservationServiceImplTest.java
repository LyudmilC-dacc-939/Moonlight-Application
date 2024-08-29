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
        endDate=LocalDate.now().plusDays(10);
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
        existingReservation.setStartDate(LocalDate.of(2024, 8, 30));
        existingReservation.setEndDate(LocalDate.of(2024, 9, 4));

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
        existingReservation.setStartDate(LocalDate.of(2024, 9, 1));
        existingReservation.setEndDate(LocalDate.of(2024, 9, 6));

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
                .createReservation(userId, roomNumber, startDate, endDate, guestsAdult, guestsChildren);

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
                    .createReservation(userId, roomNumber, LocalDate.now(), LocalDate.now().plusDays(5)
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
                    .createReservation(userId, roomNumber, LocalDate.now(), LocalDate.now().plusDays(5)
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
                    .createReservation(userId, roomNumber, startDate, endDate, guestsAdult, guestsChildren);
        });

        verify(hotelRoomRepository, times(1)).findByRoomNumber(roomNumber);
    }

    @Test
    void makeReservation_endDateBeforeStartDate() {
        LocalDate endDate = startDate.minusDays(2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(hotelRoomRepository.findByRoomNumber(roomNumber)).thenReturn(Optional.of(hotelRoom));

        assertThrows(RuntimeException.class, () -> hotelRoomReservationService
                .createReservation(userId, roomNumber, startDate, endDate, guestsAdult, guestsChildren));
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
                .createReservation(userId, roomNumber, startDate, endDate, guestsAdult, guestsChildren);

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
                .createReservation(userId, roomNumber, startDate, endDate, guestsAdult, guestsChildren);

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
            hotelRoomReservationService.createReservation(userId, roomNumber, null, endDate, guestsAdult, guestsChildren);
        });
    }

    @Test
    void makeReservation_nullEndDateShouldFail() {
        assertThrows(RuntimeException.class, () -> {
            hotelRoomReservationService.createReservation(userId, roomNumber, startDate, null, guestsAdult, guestsChildren);
        });
    }
    @Test
    void makeReservation_guestsNumberExceedRoomCapacity() {
        int guestsAdult = 6;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(hotelRoomRepository.findByRoomNumber(roomNumber)).thenReturn(Optional.of(hotelRoom));

        RoomNotAvailableException thrown = assertThrows(RoomNotAvailableException.class, () ->
                hotelRoomReservationService.createReservation(userId, roomNumber, startDate, endDate
                        , guestsAdult, guestsChildren));

        assertEquals("Total number of guests exceeds maximum allowed guest for the room type.", thrown.getMessage());
        verify(hotelRoomReservationRepository, never()).save(any(HotelRoomReservation.class));
    }


    @Test
    void getRoomReservationsByUserId_ifIdIsValid_noReservations() {
        Long userId = 1L;
        when(hotelRoomReservationRepository.findByUserIdOrderByStartDate(userId)).thenReturn(List.of());

        List<HotelRoomReservation> actualReservations = hotelRoomReservationService.getRoomReservationsByUserId(userId);

        assertNotNull(actualReservations, "The returned list should not be null");
        assertTrue(actualReservations.isEmpty(), "The returned list should be empty");
        verify(hotelRoomReservationRepository, times(1)).findByUserIdOrderByStartDate(userId);
    }

    @Test
    void getRoomReservationsByUserId_ifIdIsValid_withReservations() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        HotelRoomReservation reservation1 = new HotelRoomReservation();
        reservation1.setId(100L);
        reservation1.setUser(user);
        reservation1.setStartDate(LocalDate.of(2024, 8, 27));
        HotelRoomReservation reservation2 = new HotelRoomReservation();
        reservation2.setId(101L);
        reservation2.setUser(user);
        reservation2.setStartDate(LocalDate.of(2024, 9, 15));
        reservation2.setEndDate(LocalDate.of(2024, 10, 25));

        List<HotelRoomReservation> expectedReservations = Arrays.asList(reservation1, reservation2);
        when(hotelRoomReservationRepository.findByUserIdOrderByStartDate(userId)).thenReturn(expectedReservations);

        List<HotelRoomReservation> actualReservations = hotelRoomReservationService.getRoomReservationsByUserId(userId);
        when(hotelRoomReservationRepository.save(any(HotelRoomReservation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        when(hotelRoomReservationRepository.findByUserIdOrderByStartDate(userId)).thenReturn(expectedReservations);

        assertEquals(expectedReservations, actualReservations);
        assertNotNull(actualReservations, "The returned list should not be null");
        assertEquals(2, actualReservations.size(), "The returned list should contain 2 reservations");
        assertEquals(expectedReservations, actualReservations,
                "The returned list should match the expected reservations");
        assertEquals(100L, actualReservations.get(0).getId(),
                "The first reservation ID should be 1");
        assertEquals(LocalDate.of(2024, 8, 27), actualReservations.get(0).getStartDate(),
                "The first reservation start date should match");
        assertEquals(101L, actualReservations.get(1).getId(), "The second reservation ID should be 2");
        assertEquals(LocalDate.of(2024, 10, 25), actualReservations.get(1).getEndDate(),
                "The second reservation end date should match");
        verify(hotelRoomReservationRepository, times(1)).findByUserIdOrderByStartDate(userId);
    }

    @Test
    void getRoomReservationsByUserId_ifUserNotFound() {
        // Simulating an invalid id scenario
        long invalidUserId = 0L;
        when(hotelRoomReservationRepository.findByUserIdOrderByStartDate(invalidUserId)).thenReturn(List.of());

        List<HotelRoomReservation> actualReservations = hotelRoomReservationService.getRoomReservationsByUserId(invalidUserId);

        assertNotNull(actualReservations, "The returned list should not be null");
        assertTrue(actualReservations.isEmpty(), "The returned list should be empty");
        verify(hotelRoomReservationRepository, times(1)).findByUserIdOrderByStartDate(invalidUserId);
    }
        @Test
        void testGetAvailableRooms_ThrowsException_WhenEndDateBeforeStartDate(){
            LocalDate startDate = LocalDate.now().plusDays(5);
            LocalDate endDate = LocalDate.now().plusDays(2);

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,()-> hotelRoomReservationService
                            .getAvailableRooms(startDate, endDate));
            assertEquals("End date cannot be before start date", exception.getMessage());
        }
}