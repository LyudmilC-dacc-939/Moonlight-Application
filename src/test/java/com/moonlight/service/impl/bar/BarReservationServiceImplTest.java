package com.moonlight.service.impl.bar;

import com.moonlight.advice.exception.InvalidDateRangeException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.bar.BarReservationRequest;
import com.moonlight.dto.bar.BarReservationResponse;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import com.moonlight.model.user.User;
import com.moonlight.repository.bar.BarReservationRepository;
import com.moonlight.repository.bar.SeatRepository;
import com.moonlight.service.impl.user.CurrentUserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class BarReservationServiceImplTest {
    @Mock
    private BarReservationRepository barReservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private CurrentUserImpl currentUserImpl;

    @InjectMocks
    private BarReservationServiceImpl barReservationService;

    private User user;
    private BarReservationRequest request;
    private Seat seat;
    private Screen screen;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(); // Mocked user object
        user.setId(1L);
        user.setEmailAddress("test@test.com");
        seat = new Seat(); // Mocked seat object
        screen = Screen.SCREEN_ONE;

        request = new BarReservationRequest();
        request.setScreenName(screen.getCurrentScreenName());
        request.setSeatNumbers(Set.of(1));
        request.setReservationDate(LocalDate.now().plusDays(1));
    }

    @Test
    void testCreateReservationSeatTaken() {
        // Mock user authorization
        when(currentUserImpl.isCurrentUserMatch(any(User.class))).thenReturn(true);

        // Mock the seat being reserved
        when(seatRepository.findByScreenAndSeatNumber(any(Screen.class), anyInt())).thenReturn(Optional.of(seat));
        when(barReservationRepository.existsBySeatAndReservationDate(any(Seat.class), any(LocalDate.class))).thenReturn(true);

        // Expect UnavailableResourceException
        assertThrows(UnavailableResourceException.class, () -> barReservationService.createReservation(request, user));
    }

    @Test
    void testCreateReservationSeatOutOfRange() {
        // Change the seat number to an invalid value
        request.setSeatNumbers(Set.of(22));

        // Mock user authorization to pass
        when(currentUserImpl.isCurrentUserMatch(any(User.class))).thenReturn(true);

        // Expect UnavailableResourceException due to seat out of range
        assertThrows(UnavailableResourceException.class, () -> barReservationService.createReservation(request, user));
    }

    @Test
    void testCreateReservationDateInPast() {
        // Set the reservation date in the past
        request.setReservationDate(LocalDate.now().minusDays(1));

        // Mock user authorization to pass
        when(currentUserImpl.isCurrentUserMatch(any(User.class))).thenReturn(true);
        when(seatRepository.findByScreenAndSeatNumber(any(Screen.class), anyInt())).thenReturn(Optional.of(seat));

        // Expect InvalidDateRangeException
        assertThrows(InvalidDateRangeException.class, () -> barReservationService.createReservation(request, user));
    }

    @Test
    void testCreateReservationSuccess() {
        // Mock user authorization to pass
        when(currentUserImpl.isCurrentUserMatch(any(User.class))).thenReturn(true);

        // Create a Seat object with a screen and seat number
        Seat seat = new Seat();
        seat.setScreen(Screen.SCREEN_ONE);
        seat.setSeatNumber(1); // Seat number 1 for SCREEN_ONE

        // Mock SeatRepository to return the seat when searching by screen and seat number
        when(seatRepository.findByScreenAndSeatNumber(Screen.SCREEN_ONE, 1)).thenReturn(Optional.of(seat));

        // Mock the BarReservationRequest
        request.setSeatNumbers(Set.of(1));
        request.setScreenName("SCREEN: Football");
        request.setReservationDate(LocalDate.now().plusDays(1)); // Future date

        // Create a BarReservation object to mock the saved result
        BarReservation barReservation = new BarReservation();
        barReservation.setId(1L); // Simulate generated ID after save
        barReservation.setSeats(Set.of(seat));
        barReservation.setScreen(Screen.SCREEN_ONE);
        barReservation.setReservationDate(LocalDate.now().plusDays(1));
        barReservation.setTotalCost(5.0);

        // Mock the save method to return the saved reservation
        when(barReservationRepository.save(any(BarReservation.class))).thenReturn(barReservation);

        // Execute the reservation
        BarReservationResponse response = barReservationService.createReservation(request, user);

        // Verify the response contains the correct seat numbers
        assertEquals(Set.of(1), response.getSeatNumbers()); // Expecting seat 1
        assertEquals(1L, response.getReservationId()); // Expecting reservation ID 1
    }
    @Test
    void testGetBarReservationsByUserId() {
        User user = new User();
        user.setId(1L); // Set an ID for the user

        BarReservation reservation1 = new BarReservation();
        reservation1.setId(1L); // Example ID
        reservation1.setUser(user); // Associate the reservation with the user
        // Set other properties as necessary

        BarReservation reservation2 = new BarReservation();
        reservation2.setId(2L);
        reservation2.setUser(user); // Associate the reservation with the user

        List<BarReservation> mockReservations = List.of(reservation1, reservation2);
        when(barReservationRepository.findByUserId(anyLong())).thenReturn(mockReservations);

        List<BarReservation> reservations = barReservationService.getBarReservationsByUserId(1L);

        assertThat(reservations).hasSize(2);
        assertThat(reservations).extracting("id").containsExactly(1L, 2L);
    }

    @Test
    public void testGetAvailableSeats() {
        Screen screen = Screen.SCREEN_ONE;
        LocalDate reservationDate = LocalDate.now().plusDays(1);

        Seat seat1 = new Seat();
        seat1.setId(1L);
        seat1.setSeatNumber(1);
        seat1.setScreen(Screen.SCREEN_ONE);

        Seat seat2 = new Seat();
        seat1.setId(2L);
        seat1.setSeatNumber(2);
        seat1.setScreen(Screen.SCREEN_ONE);

        Seat seat3 = new Seat();
        seat1.setId(3L);
        seat1.setSeatNumber(3);
        seat1.setScreen(Screen.SCREEN_ONE);

        List<Seat> allSeats = Arrays.asList(seat1,seat2,seat3);

        Set<Seat> reservedSeats = new HashSet<>();
        reservedSeats.add(allSeats.get(0));
        BarReservation reservation = new BarReservation();
        reservation.setSeats(reservedSeats);

        when(seatRepository.findByScreen(screen)).thenReturn(allSeats);
        when(barReservationRepository.findByScreenAndReservationDate(screen, reservationDate))
                .thenReturn(Collections.singletonList(reservation));
        List<Seat> availableSeats = barReservationService
                .getAvailableSeats(screen.getCurrentScreenName(), reservationDate);
        assertEquals(2, availableSeats.size());
        assertTrue(availableSeats.contains(allSeats.get(1)));
        assertTrue(availableSeats.contains(allSeats.get(2)));
        assertFalse(availableSeats.contains(allSeats.get(0)));
    }
}