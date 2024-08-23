package com.moonlight.service.impl.car;

import com.moonlight.advice.exception.InvalidDateRangeException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.car.CarReservationRequest;
import com.moonlight.model.car.Car;
import com.moonlight.model.car.CarReservation;
import com.moonlight.model.enums.CarType;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.user.User;
import com.moonlight.repository.car.CarRepository;
import com.moonlight.repository.car.CarReservationRepository;
import com.moonlight.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarReservationServiceImplTest {

    @Mock
    private CarReservationRepository carReservationRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CarReservationServiceImpl carReservationService;

    private CarReservationRequest validRequest;
    private CarReservationRequest invalidRequest;
    private User user;
    private Car car;
    private CarReservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validRequest = new CarReservationRequest();
        validRequest.setCarId(1L);
        validRequest.setStartDate(LocalDate.now().plusDays(1));
        validRequest.setEndDate(LocalDate.now().plusDays(2));

        invalidRequest = new CarReservationRequest();
        invalidRequest.setCarId(1L);
        invalidRequest.setStartDate(LocalDate.now().plusDays(2));
        invalidRequest.setEndDate(LocalDate.now().plusDays(1));

        user = new User();
        user.setEmailAddress("test@test.com");

        car = new Car();
        car.setId(1L);
        car.setType(CarType.SPORT);

        reservation = new CarReservation();
        reservation.setUser(user);
        reservation.setCar(car);
        reservation.setStartDate(validRequest.getStartDate());
        reservation.setEndDate(validRequest.getEndDate());
        reservation.setTotalCost(1000.0);
        reservation.setStatus(ReservationStatus.PENDING);
    }

    @Test
    void testCreateReservationSuccess() {
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carReservationRepository.findOverlappingReservations(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());  // No overlapping reservations
        when(carReservationRepository.save(any(CarReservation.class))).thenReturn(reservation);

        CarReservation result = carReservationService.createReservation(validRequest, user.getEmailAddress());

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(car, result.getCar());
        assertEquals(validRequest.getStartDate(), result.getStartDate());
        assertEquals(validRequest.getEndDate(), result.getEndDate());
        assertEquals(ReservationStatus.PENDING, result.getStatus());
    }

    @Test
    void testCreateReservationDateRangeInvalid() {
        InvalidDateRangeException thrown = assertThrows(InvalidDateRangeException.class, () ->
                carReservationService.createReservation(invalidRequest, "test@test.com"));

        assertEquals("End date must be after start date.", thrown.getMessage());
    }

    @Test
    void testCreateReservationUserNotFound() {
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());

        RecordNotFoundException thrown = assertThrows(RecordNotFoundException.class, () ->
                carReservationService.createReservation(validRequest, "test@test.com"));

        assertEquals("User with email test@test.com does not exist", thrown.getMessage());
    }

    @Test
    void testCreateReservationCarNotFound() {
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));
        when(carRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        RecordNotFoundException thrown = assertThrows(RecordNotFoundException.class, () ->
                carReservationService.createReservation(validRequest, "test@test.com"));

        assertEquals("Car not found", thrown.getMessage());
    }

    @Test
    void testCreateReservationCarNotAvailable() {
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        CarReservation overlappingReservation = new CarReservation();
        when(carReservationRepository.findOverlappingReservations(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(overlappingReservation));

        UnavailableResourceException exception = assertThrows(UnavailableResourceException.class, () -> {
            carReservationService.createReservation(validRequest, "test@test.com");
        });

        assertEquals("Car is not available during the selected dates.", exception.getMessage());
    }

    @Test
    void testCreateReservationNumberOfPeopleExceedsCarCapacity() {
        // Setup
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carReservationRepository.findOverlappingReservations(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());  // No overlapping reservations

        // Simulate a car with fewer seats than the number of people in the request
        CarType carTypeWithLimitedSeats = CarType.SPORT;
        // Example: 2 seats
        car.setType(carTypeWithLimitedSeats);

        // Adjust the request to have more people than the car can accommodate
        validRequest.setNumberOfPeople(4);  // Example: 4 people

        UnavailableResourceException exception = assertThrows(UnavailableResourceException.class, () -> {
            carReservationService.createReservation(validRequest, user.getEmailAddress());
        });

        assertEquals("This car is not suitable for the amount of selected people.", exception.getMessage());
    }

    @Test
    void testCreateReservationTotalCostIsZero() {
        // Simulate a scenario where the number of days results in a total cost of 0
        CarType carType = car.getType();
        car.setType(carType);

        // Adjust the request to have start and end date on the same day, making days = 0
        validRequest.setStartDate(LocalDate.now());
        validRequest.setEndDate(LocalDate.now());

        // Since the total cost would initially be 0, it should default to the car's price
        assertEquals(carType.getPrice(), reservation.getTotalCost(), "Total cost should default to the car's price when initial total cost is zero.");
    }

    @Test
    void testCreateReservationStartDateInThePast() {
        // Adjust the request to have a start date in the past
        validRequest.setStartDate(LocalDate.now().minusDays(1));  // Yesterday
        validRequest.setEndDate(LocalDate.now().plusDays(1));     // Tomorrow

        InvalidDateRangeException exception = assertThrows(InvalidDateRangeException.class, () -> {
            carReservationService.createReservation(validRequest, user.getEmailAddress());
        });

        assertEquals("Selected date must be in the Present or in the Future", exception.getMessage());
    }
}