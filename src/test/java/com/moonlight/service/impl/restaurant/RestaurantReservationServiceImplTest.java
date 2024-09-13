package com.moonlight.service.impl.restaurant;

import com.moonlight.advice.exception.InvalidDateRangeException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.restaurant.RestaurantReservationRequest;
import com.moonlight.model.car.CarReservation;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.enums.RestaurantZone;
import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;
import com.moonlight.repository.restaurant.RestaurantRepository;
import com.moonlight.repository.restaurant.RestaurantReservationRepository;
import com.moonlight.service.impl.user.CurrentUserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        restaurant = new Restaurant();
        LocalDate reservationDate = LocalDate.now().plusDays(1);
        LocalTime reservationTime = LocalTime.of(13, 0);

        request = new RestaurantReservationRequest();
        request.setTableNumber(14L);
        request.setNumberOfPeople(2);
        request.setSmoking(true);
        request.setReservationDate(reservationDate);
        request.setReservationTime(reservationTime);

    }
    @Test
    public void testCreateReservation_UserNotAuthorized() {
      when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(false);

      Exception exception = assertThrows(RecordNotFoundException.class,()->restaurantReservationService.createReservation(request,user));

        assertEquals("This user is not authorize to proceed this operation",exception.getMessage());
    }
    @Test
    public void testCreateReservation_TableNotFound() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RecordNotFoundException.class,()->restaurantReservationService.createReservation(request,user));

        assertEquals("Table not found",exception.getMessage());
    }
    @Test
    public void testCreateReservation_RequestExceedTableSeats() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.of(restaurant));

        restaurant.setMaxNumberOfSeats(1);

        Exception exception = assertThrows(UnavailableResourceException.class,()->restaurantReservationService.createReservation(request,user));

        assertEquals("This table is not suitable for the number of selected people.",exception.getMessage());
    }
    @Test
    public void testCreateReservation_IsNotSmoking() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.of(restaurant));
        restaurant.setMaxNumberOfSeats(3);
        restaurant.setRestaurantZone(RestaurantZone.SALOON);

        Exception exception = assertThrows(UnavailableResourceException.class,()->restaurantReservationService.createReservation(request,user));
        assertEquals("This table isn't in smoking area",exception.getMessage());
    }

    @Test
    public void testCreateReservation_DateIsBefore() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.of(restaurant));
        restaurant.setMaxNumberOfSeats(3);
        restaurant.setRestaurantZone(RestaurantZone.TERRACE);
        when(restaurantReservationRepository.alreadyExistingReservation(any(),any(),any(),any(),any())).thenReturn(0);

        request.setReservationDate(LocalDate.now().minusDays(1));

        Exception exception = assertThrows(InvalidDateRangeException.class,()->restaurantReservationService.createReservation(request,user));

        assertEquals("Reservation date and time can't be in the past",exception.getMessage());

    }
    @Test
    public void testCreateReservation_ExistingReservation() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.of(restaurant));
        restaurant.setMaxNumberOfSeats(3);
        restaurant.setRestaurantZone(RestaurantZone.TERRACE);
        when(restaurantReservationRepository.alreadyExistingReservation(any(),any(),any(),any(),any())).thenReturn(1);

        Exception exception = assertThrows(InvalidDateRangeException.class,()->restaurantReservationService.createReservation(request,user));

        assertEquals("Reservation already exists for this table, try for different hour",exception.getMessage());

    }
    @Test
    public void testCreateReservation_Success() {
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(restaurantRepository.findByTableNumber(request.getTableNumber())).thenReturn(Optional.of(restaurant));
        restaurant.setMaxNumberOfSeats(3);
        restaurant.setRestaurantZone(RestaurantZone.TERRACE);
        when(restaurantReservationRepository.alreadyExistingReservation(any(),any(),any(),any(),any())).thenReturn(0);
        when(restaurantReservationRepository.save(any(RestaurantReservation.class))).thenAnswer(i->i.getArguments()[0]);

        RestaurantReservation reservation = restaurantReservationService.createReservation(request,user);

        assertNotNull(reservation);
        assertEquals(user,reservation.getUser());
        assertEquals(restaurant,reservation.getRestaurant());
        assertEquals(request.getReservationDate(),reservation.getReservationDate());
        assertEquals(LocalDateTime.of(request.getReservationDate(),request.getReservationTime()),reservation.getReservationTime());
        assertEquals(LocalDateTime.of(request.getReservationDate(),request.getReservationTime().plusHours(1)),reservation.getReservationEndTime());
        assertEquals(restaurant.getRestaurantZone(),reservation.getZone());
        assertEquals(restaurant.getRestaurantZone().getSeatPrice()*request.getNumberOfPeople(),reservation.getSeatCost());
        assertEquals(request.getTableNumber(),reservation.getTableNumber());
        assertEquals(request.isSmoking(),reservation.isSmoking());

    }
}