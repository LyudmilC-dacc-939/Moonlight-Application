package com.moonlight.service.impl.restaurant;

import com.moonlight.advice.exception.InvalidDateRangeException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.restaurant.RestaurantReservationRequest;
import com.moonlight.dto.restaurant.TableAvailabilityResponse;
import com.moonlight.model.enums.RestaurantZone;
import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;
import com.moonlight.repository.restaurant.RestaurantRepository;
import com.moonlight.repository.restaurant.RestaurantReservationRepository;
import com.moonlight.service.impl.user.CurrentUserImpl;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RestaurantReservationServiceImplTest {

    @Mock
    private RestaurantReservationRepository restaurantReservationRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private CurrentUserImpl currentUserImpl;
    @InjectMocks
    private RestaurantReservationServiceImpl restaurantReservationService;
    private User user;
    private Restaurant restaurant;
    private RestaurantReservation reservation;
    private RestaurantReservationRequest request;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer seats;
    private Boolean isSmoking;
    private Restaurant table1;
    private Restaurant table2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        restaurant = new Restaurant();
        reservationDate = LocalDate.now().plusDays(1);
        LocalTime reservationTime = LocalTime.of(13, 0);

        request = new RestaurantReservationRequest();
        request.setTableNumber(14L);
        request.setNumberOfPeople(2);
        request.setSmoking(true);
        request.setReservationDate(reservationDate);
        request.setReservationTime(reservationTime);


        startTime = LocalTime.of(10, 0);
        endTime = LocalTime.of(23, 0);
        seats = 4;
        isSmoking = false;

        RestaurantReservation mockReservation = new RestaurantReservation();
        mockReservation.setRestaurant(table1);
        mockReservation.setReservationDate(reservationDate);
        mockReservation.setReservationTime(LocalDateTime.of(reservationDate, LocalTime.of(13, 0)));
        mockReservation.setReservationEndTime(LocalDateTime.of(reservationDate, LocalTime.of(14, 0)));

        table1 = new Restaurant();
        table1.setTableNumber(1L);
        table1.setRestaurantZone(RestaurantZone.SALOON);
        table1.setMaxNumberOfSeats(4);

        table2 = new Restaurant();
        table2.setTableNumber(1L);
        table2.setRestaurantZone(RestaurantZone.SALOON);
        table2.setMaxNumberOfSeats(4);

        when(restaurantReservationRepository.findAvailableTablesByDateAndPreferences(
                reservationDate, seats, isSmoking))
                .thenReturn(Collections.singletonList(new Object[]{1L, "13:00", "14:00"}));

        when(restaurantRepository.findAll()).thenReturn(Lists.list(table1, table2));
    }

    @Test
    public void testCreateReservation_UserNotAuthorized() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(false);

        Exception exception = assertThrows(RecordNotFoundException.class, () -> restaurantReservationService.createReservation(request, user));

        assertEquals("This user is not authorize to proceed this operation", exception.getMessage());
    }

    private RestaurantReservation createReservation(Long id, LocalDate date, int hour, int minute) {
        RestaurantReservation reservation = new RestaurantReservation();
        reservation.setId(id);
        reservation.setReservationDate(date);
        reservation.setReservationTime(LocalDateTime.of(date, LocalTime.of(hour, minute)));
        reservation.setTableNumber(request.getTableNumber());
        reservation.setSmoking(request.isSmoking());
        reservation.setUser(user);
        reservation.setRestaurant(restaurant);
        return reservation;
    }

    @Test
    public void testCreateReservation_TableNotFound() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RecordNotFoundException.class, () -> restaurantReservationService.createReservation(request, user));

        assertEquals("Table not found", exception.getMessage());
    }

    @Test
    public void testCreateReservation_RequestExceedTableSeats() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.of(restaurant));

        restaurant.setMaxNumberOfSeats(1);

        Exception exception = assertThrows(UnavailableResourceException.class, () -> restaurantReservationService.createReservation(request, user));

        assertEquals("This table is not suitable for the number of selected people.", exception.getMessage());
    }

    @Test
    public void testCreateReservation_IsNotSmoking() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.of(restaurant));
        restaurant.setMaxNumberOfSeats(3);
        restaurant.setRestaurantZone(RestaurantZone.SALOON);

        Exception exception = assertThrows(UnavailableResourceException.class, () -> restaurantReservationService.createReservation(request, user));
        assertEquals("This table isn't in smoking area", exception.getMessage());
    }

    @Test
    public void testCreateReservation_DateIsBefore() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.of(restaurant));
        restaurant.setMaxNumberOfSeats(3);
        restaurant.setRestaurantZone(RestaurantZone.TERRACE);

        when(restaurantReservationRepository.alreadyExistingReservation(any(), any(), any(), any(), any())).thenReturn(0);

        request.setReservationDate(LocalDate.now().minusDays(1));

        Exception exception = assertThrows(InvalidDateRangeException.class, () -> restaurantReservationService.createReservation(request, user));

        assertEquals("Reservation date and time can't be in the past", exception.getMessage());

    }

    @Test
    public void testCreateReservation_ExistingReservation() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.of(restaurant));
        restaurant.setMaxNumberOfSeats(3);
        restaurant.setRestaurantZone(RestaurantZone.TERRACE);

        when(restaurantReservationRepository.alreadyExistingReservation(any(), any(), any(), any(), any())).thenReturn(1);

        Exception exception = assertThrows(InvalidDateRangeException.class, () -> restaurantReservationService.createReservation(request, user));

        assertEquals("Reservation already exists for this table, try for different hour", exception.getMessage());
    }

    @Test
    public void testCreateReservation_Success() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.of(restaurant));
        restaurant.setMaxNumberOfSeats(3);
        restaurant.setRestaurantZone(RestaurantZone.TERRACE);

        when(restaurantReservationRepository.alreadyExistingReservation(any(), any(), any(), any(), any())).thenReturn(0);
        when(restaurantReservationRepository.save(any(RestaurantReservation.class))).thenAnswer(i -> i.getArguments()[0]);

        RestaurantReservation reservation = restaurantReservationService.createReservation(request, user);

        assertNotNull(reservation);
        assertEquals(user, reservation.getUser());
        assertEquals(restaurant, reservation.getRestaurant());
        assertEquals(request.getReservationDate(), reservation.getReservationDate());
        assertEquals(LocalDateTime.of(request.getReservationDate(), request.getReservationTime()), reservation.getReservationTime());
        assertEquals(LocalDateTime.of(request.getReservationDate(), request.getReservationTime().plusHours(1)), reservation.getReservationEndTime());
        assertEquals(restaurant.getRestaurantZone(), reservation.getZone());
        assertEquals(restaurant.getRestaurantZone().getSeatPrice() * request.getNumberOfPeople(), reservation.getSeatCost());
        assertEquals(request.getTableNumber(), reservation.getTableNumber());
        assertEquals(request.isSmoking(), reservation.isSmoking());
    }

    @Test
    public void testGetAvailableTablesByDateAndPreferences_NotAvailableTables() {
        table1 = new Restaurant();
        table1.setTableNumber(1L);
        table1.setRestaurantZone(RestaurantZone.SALOON);
        table1.setMaxNumberOfSeats(4);

        RestaurantReservation mockReservation = new RestaurantReservation();
        mockReservation.setRestaurant(table1);
        mockReservation.setReservationDate(reservationDate);
        mockReservation.setReservationTime(LocalDateTime.of(reservationDate, LocalTime.of(9, 0)));
        mockReservation.setReservationEndTime(LocalDateTime.of(reservationDate, LocalTime.of(23, 0)));

        when(restaurantReservationRepository.findAvailableTablesByDateAndPreferences(
                reservationDate, seats, isSmoking))
                .thenReturn(Collections.singletonList(new Object[]{1L,
                        Timestamp.valueOf(LocalDateTime.of(reservationDate, LocalTime.of(9, 0))),
                        Timestamp.valueOf(LocalDateTime.of(reservationDate, LocalTime.of(23, 30)))}));

        when(restaurantRepository.findAll()).thenReturn(Collections.singletonList(table1));

        when(restaurantRepository.getReferenceById(table1.getTableNumber())).thenReturn(table1);

        TableAvailabilityResponse response = restaurantReservationService
                .getAvailableTablesByDateAndPreferences(
                        reservationDate, startTime, endTime, seats, isSmoking);

        Map<String, List<String[]>> actualAvailableSlots = response.getAvailableTables();

        List<String[]> actualSlots = actualAvailableSlots.get("Table number: 1, zone: SALOON");

        assertNotNull(response);
        assertEquals(reservationDate, response.getQueryForDate());
        assertTrue(actualSlots.isEmpty());
    }

    @Test
    public void testGetAvailableTablesByDateAndPreferences_FullPeriodAvailable() {
        table1 = new Restaurant();
        table1.setTableNumber(1L);
        table1.setRestaurantZone(RestaurantZone.SALOON);
        table1.setMaxNumberOfSeats(4);

        when(restaurantReservationRepository.findAvailableTablesByDateAndPreferences(
                reservationDate, seats, isSmoking))
                .thenReturn(Collections.emptyList());

        when(restaurantRepository.findAll()).thenReturn(Collections.singletonList(table1));

        when(restaurantRepository.getReferenceById(table1.getTableNumber())).thenReturn(table1);

        List<String[]> expectedAvailableSlots = Collections.singletonList(new String[]{"10:00", "23:00"});

        Map<String, List<String[]>> expectedAvailableTables = new LinkedHashMap<>();
        expectedAvailableTables.put("Table number: 1, zone: SALOON", expectedAvailableSlots);

        List<String[]> expectedSlots = expectedAvailableTables.get("Table number: 1, zone: SALOON");

        TableAvailabilityResponse response = restaurantReservationService
                .getAvailableTablesByDateAndPreferences(
                        reservationDate, startTime, endTime, seats, isSmoking);

        Map<String, List<String[]>> actualAvailableSlots = response.getAvailableTables();

        List<String[]> actualSlots = actualAvailableSlots.get("Table number: 1, zone: SALOON");

        assertNotNull(response);
        assertEquals(reservationDate, response.getQueryForDate());
        assertArrayEquals(expectedSlots.get(0), actualSlots.get(0), "Mismatch in first slot");
    }

    @Test
    public void testGetAvailableTablesByDateAndPreferences_ExceedSeats() {
        when(restaurantReservationRepository.findAvailableTablesByDateAndPreferences(reservationDate, 6, isSmoking))
                .thenReturn(Collections.emptyList());
        TableAvailabilityResponse response = restaurantReservationService
                .getAvailableTablesByDateAndPreferences(reservationDate
                        , startTime, endTime, 6, isSmoking);

        assertNotNull(response);
        assertTrue(response.getAvailableTables().isEmpty());
    }

    @Test
    public void testGetAvailableTablesByDateAndPreferences_Success() {
        table1 = new Restaurant();
        table1.setTableNumber(1L);
        table1.setRestaurantZone(RestaurantZone.SALOON);
        table1.setMaxNumberOfSeats(4);

        RestaurantReservation mockReservation = new RestaurantReservation();
        mockReservation.setRestaurant(table1);
        mockReservation.setReservationDate(reservationDate);
        mockReservation.setReservationTime(LocalDateTime.of(reservationDate, LocalTime.of(13, 0)));
        mockReservation.setReservationEndTime(LocalDateTime.of(reservationDate, LocalTime.of(14, 0)));

        when(restaurantReservationRepository.findAvailableTablesByDateAndPreferences(
                reservationDate, seats, isSmoking))
                .thenReturn(Collections.singletonList(new Object[]{1L,
                        Timestamp.valueOf(LocalDateTime.of(reservationDate, LocalTime.of(13, 0))),
                        Timestamp.valueOf(LocalDateTime.of(reservationDate, LocalTime.of(14, 0)))}));

        when(restaurantRepository.findAll()).thenReturn(Collections.singletonList(table1));

        when(restaurantRepository.getReferenceById(table1.getTableNumber())).thenReturn(table1);

        List<String[]> expectedAvailableSlots = List.of(new String[]{"10:00", "13:00"}
                , new String[]{"14:00", "23:00"});

        Map<String, List<String[]>> expectedAvailableTables = new LinkedHashMap<>();
        expectedAvailableTables.put("Table number: 1, zone: SALOON", expectedAvailableSlots);

        List<String[]> expectedSlots = expectedAvailableTables.get("Table number: 1, zone: SALOON");

        TableAvailabilityResponse response = restaurantReservationService
                .getAvailableTablesByDateAndPreferences(reservationDate, startTime, endTime, seats, isSmoking);
        Map<String, List<String[]>> actualAvailableSlots = response.getAvailableTables();

        List<String[]> actualSlots = actualAvailableSlots.get("Table number: 1, zone: SALOON");

        assertNotNull(actualAvailableSlots);

        assertNotNull(expectedSlots);
        assertNotNull(actualSlots);

        assertEquals(reservationDate, response.getQueryForDate());

        assertArrayEquals(expectedSlots.get(0), actualSlots.get(0), "Mismatch in first slot");
        assertArrayEquals(expectedSlots.get(1), actualSlots.get(1), "Mismatch in second slot");
    }

    @Test
    public void testGetAvailableTablesByDateAndPreferences_DateIsNull() {
        table1 = new Restaurant();
        table1.setTableNumber(1L);
        table1.setRestaurantZone(RestaurantZone.SALOON);
        table1.setMaxNumberOfSeats(4);

        RestaurantReservation mockReservation = new RestaurantReservation();
        mockReservation.setRestaurant(table1);
        mockReservation.setReservationDate(reservationDate.minusDays(1));
        mockReservation.setReservationTime(LocalDateTime.of(reservationDate, LocalTime.now()));
        mockReservation.setReservationEndTime(LocalDateTime.of(reservationDate, LocalTime.now().plusHours(1)));

        when(restaurantRepository.findAll()).thenReturn(Collections.singletonList(table1));

        when(restaurantRepository.getReferenceById(table1.getTableNumber())).thenReturn(table1);

        TableAvailabilityResponse response = restaurantReservationService
                .getAvailableTablesByDateAndPreferences(null, null, endTime, seats, isSmoking);
        Map<String, List<String[]>> actualAvailableSlots = response.getAvailableTables();

        assertNotNull(response);
        assertEquals(LocalDate.now(), response.getQueryForDate());
        assertFalse(actualAvailableSlots.isEmpty());
    }

    @Test
    public void testGetRestaurantReservationsByUserId_WithExistingReservations() {
        Long userId = 1L;
        RestaurantReservation reservation1 = createReservation(1L, LocalDate.now().plusDays(1), 13, 0);
        RestaurantReservation reservation2 = createReservation(2L, LocalDate.now().plusDays(2), 14, 0);

        List<RestaurantReservation> reservations = Arrays.asList(reservation1, reservation2);

        when(restaurantReservationRepository.findByUserIdOrderByReservationDateReservationDateAsc(userId))
                .thenReturn(reservations);

        List<RestaurantReservation> result = restaurantReservationService.getRestaurantReservationsByUserId(userId);

        assertEquals(2, result.size(), "Should return 2 reservations");
        assertEquals(reservation1.getReservationDate(), result.get(0).getReservationDate());
        assertEquals(reservation2.getReservationDate(), result.get(1).getReservationDate());
        verify(restaurantReservationRepository, times(1))
                .findByUserIdOrderByReservationDateReservationDateAsc(userId);
    }

    @Test
    public void testGetRestaurantReservationsByUserId_NoReservations() {
        Long userId = 1L;

        when(restaurantReservationRepository.findByUserIdOrderByReservationDateReservationDateAsc(userId))
                .thenReturn(Collections.emptyList());

        List<RestaurantReservation> result = restaurantReservationService.getRestaurantReservationsByUserId(userId);

        assertTrue(result.isEmpty(), "The result should be an empty list");
        verify(restaurantReservationRepository, times(1))
                .findByUserIdOrderByReservationDateReservationDateAsc(userId);
    }

    @Test
    public void testGetRestaurantReservationsByUserId_NullUserId() {
        // Arrange
        RestaurantReservation reservation1 = createReservation(1L, LocalDate.now().plusDays(1), 13, 0);
        RestaurantReservation reservation2 = createReservation(2L, LocalDate.now().plusDays(2), 14, 0);

        List<RestaurantReservation> reservations = Arrays.asList(reservation1, reservation2);

        when(restaurantReservationRepository.findByUserIdOrderByReservationDateReservationDateAsc(null))
                .thenReturn(reservations);

        List<RestaurantReservation> result = restaurantReservationService.getRestaurantReservationsByUserId(null);

        assertEquals(2, result.size(), "Should return all reservations when userId is null");
        verify(restaurantReservationRepository, times(1))
                .findByUserIdOrderByReservationDateReservationDateAsc(null);
    }

    @Test
    public void testGetRestaurantReservationsByUserId_MultipleReservationsSameDay() {
        Long userId = 1L;
        RestaurantReservation reservation1 = createReservation(1L, LocalDate.now().plusDays(1), 13, 0);
        RestaurantReservation reservation2 = createReservation(2L, LocalDate.now().plusDays(1), 14, 0);

        List<RestaurantReservation> reservations = Arrays.asList(reservation1, reservation2);

        when(restaurantReservationRepository.findByUserIdOrderByReservationDateReservationDateAsc(userId))
                .thenReturn(reservations);

        List<RestaurantReservation> result = restaurantReservationService.getRestaurantReservationsByUserId(userId);

        assertEquals(2, result.size(), "Should return 2 reservations");
        assertEquals(reservation1.getReservationTime(), result.get(0).getReservationTime());
        assertEquals(reservation2.getReservationTime(), result.get(1).getReservationTime());
        verify(restaurantReservationRepository, times(1))
                .findByUserIdOrderByReservationDateReservationDateAsc(userId);
    }
}