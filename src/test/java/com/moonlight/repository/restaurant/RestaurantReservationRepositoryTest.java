package com.moonlight.repository.restaurant;

import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.enums.RestaurantZone;
import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;
import com.moonlight.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.mockito.Mock;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJpaTest
@ActiveProfiles("test")
class RestaurantReservationRepositoryTest {

    @Autowired
    private RestaurantReservationRepository restaurantReservationRepository;
    @Mock
    private RestaurantReservationRepository restaurantReservationRepositoryMocked;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    private User user;
    private Restaurant restaurant;
    private RestaurantReservation restaurantReservation;
    private LocalDate reservationDate;
    private Integer seats;
    private Boolean isSmoking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmailAddress("test@test.com");
        user.setPassword("password123");
        user.setPhoneNumber("123456789");
        userRepository.save(user);

        restaurant = new Restaurant();
        restaurant.setTableNumber(1L);
        restaurant.setMaxNumberOfSeats(4);
        restaurant.setRestaurantZone(RestaurantZone.BAR);
        restaurantRepository.save(restaurant);

        reservationDate = LocalDate.now().plusDays(1);
        LocalDate reservationDate = LocalDate.now().plusDays(1);
        LocalTime reservationTime = LocalTime.of(13, 0);
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, reservationTime);

        restaurantReservation = new RestaurantReservation();
        restaurantReservation.setUser(user);
        restaurantReservation.setRestaurant(restaurant);
        restaurantReservation.setReservationTime(reservationDateTime);
        restaurantReservation.setReservationEndTime(reservationDateTime.plusHours(1));
        restaurantReservation.setReservationDate(LocalDate.now());
        restaurantReservation.setTableNumber(1L);
        restaurantReservation.setZone(RestaurantZone.BAR);
        restaurantReservation.setStatus(ReservationStatus.PENDING);
        restaurantReservationRepository.save(restaurantReservation);

        seats = 1;
        isSmoking = false;
    }

    @Test
    void testFindByUserId() {
        List<RestaurantReservation> reservations = restaurantReservationRepository.findByUserId(user.getId());
        assertFalse(reservations.isEmpty());
        assertEquals(1, reservations.size());
        assertEquals(user.getId(), reservations.get(0).getUser().getId());
    }

    @Test
    public void testAlreadyExistingReservation_Exists_ReturnsOne() {
        int result = restaurantReservationRepository.alreadyExistingReservation(
                restaurant.getId(),
                restaurantReservation.getReservationDate(),
                restaurantReservation.getReservationTime(),
                restaurantReservation.getReservationEndTime(),
                restaurantReservation.getTableNumber());

        assertEquals(1, result);
    }

    @Test
    public void testAlreadyExistingReservation_NonExisting_ReturnsZero() {
        LocalDateTime newReservationTime = restaurantReservation.getReservationTime().plusHours(2);
        LocalDateTime newReservationEndTime = restaurantReservation.getReservationEndTime().plusHours(1);

        int result = restaurantReservationRepository.alreadyExistingReservation(
                restaurant.getId(),
                restaurantReservation.getReservationDate(),
                newReservationTime,
                newReservationEndTime,
                restaurantReservation.getTableNumber());

        assertEquals(0, result);
    }

    @Test
    public void testAlreadyExistingReservation_OverLapping_ReturnsOne() {
        LocalDateTime newReservationTime = restaurantReservation.getReservationTime().plusMinutes(30);
        LocalDateTime newReservationEndTime = restaurantReservation.getReservationEndTime().plusMinutes(30);

        int result = restaurantReservationRepository.alreadyExistingReservation(
                restaurant.getId(),
                restaurantReservation.getReservationDate(),
                newReservationTime,
                newReservationEndTime,
                restaurantReservation.getTableNumber());

        assertEquals(1, result);
    }

    @Test
    public void testAlreadyExistingReservation_OverLapping_ReturnsZero() {
        LocalDateTime newReservationTime = restaurantReservation.getReservationTime().plusHours(1);
        LocalDateTime newReservationEndTime = restaurantReservation.getReservationEndTime().plusHours(1);

        int result = restaurantReservationRepository.alreadyExistingReservation(
                restaurant.getId(),
                restaurantReservation.getReservationDate(),
                newReservationTime,
                newReservationEndTime,
                restaurantReservation.getTableNumber());

        assertEquals(0, result);
    }

    @Test
    public void testAlreadyExistingReservation_DifferentTable_ReturnsZero() {

        Long diffTable = restaurantReservation.getTableNumber() + 1L;

        int result = restaurantReservationRepository.alreadyExistingReservation(
                restaurant.getId(),
                restaurantReservation.getReservationDate(),
                restaurantReservation.getReservationTime(),
                restaurantReservation.getReservationEndTime(),
                diffTable);

        assertEquals(0, result);
    }

    @Test
    public void testFindByUserIdOrderByReservationDateReservationDateAsc_MultipleReservationsSameDay() {
        Long userId = 1L;
        RestaurantReservation reservation1 = new RestaurantReservation();
        reservation1.setId(1L);
        reservation1.setReservationDate(LocalDate.of(2023, 9, 12));
        reservation1.setReservationTime(LocalDateTime.of(2023, 9, 20, 19, 0));

        RestaurantReservation reservation2 = new RestaurantReservation();
        reservation2.setId(2L);
        reservation2.setReservationDate(LocalDate.of(2023, 9, 12));
        reservation2.setReservationTime(LocalDateTime.of(2023, 9, 20, 20, 0));

        List<RestaurantReservation> reservations = Arrays.asList(reservation1, reservation2);

        when(restaurantReservationRepositoryMocked.findByUserIdOrderByReservationDateReservationDateAsc(userId))
                .thenReturn(reservations);

        List<RestaurantReservation> result = restaurantReservationRepositoryMocked.findByUserIdOrderByReservationDateReservationDateAsc(userId);

        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2023, 9, 12), result.get(0).getReservationDate());
        assertEquals(LocalDate.of(2023, 9, 12), result.get(1).getReservationDate());
        assertEquals(LocalDateTime.of(2023, 9, 20, 19, 0), result.get(0).getReservationTime());
        assertEquals(LocalDateTime.of(2023, 9, 20, 20, 0), result.get(1).getReservationTime());
        verify(restaurantReservationRepositoryMocked, times(1)).findByUserIdOrderByReservationDateReservationDateAsc(userId);
    }

    @Test
    public void testFindByUserIdOrderByReservationDateReservationDateAsc_NoReservations() {
        Long userId = 2L;

        when(restaurantReservationRepositoryMocked.findByUserIdOrderByReservationDateReservationDateAsc(userId))
                .thenReturn(List.of());

        List<RestaurantReservation> result = restaurantReservationRepositoryMocked.findByUserIdOrderByReservationDateReservationDateAsc(userId);

        assertTrue(result.isEmpty(), "The reservation list should be empty when no reservations exist");
        verify(restaurantReservationRepositoryMocked, times(1)).findByUserIdOrderByReservationDateReservationDateAsc(userId);
    }

    @Test
    public void testFindByUserIdOrderByReservationDateReservationDateAsc_NullUserId() {
        RestaurantReservation reservation1 = new RestaurantReservation();
        reservation1.setId(1L);
        reservation1.setReservationDate(LocalDate.of(2023, 9, 15));
        reservation1.setReservationTime(LocalDateTime.of(2023, 9, 15, 19, 0));

        RestaurantReservation reservation2 = new RestaurantReservation();
        reservation2.setId(2L);
        reservation2.setReservationDate(LocalDate.of(2023, 9, 20));
        reservation2.setReservationTime(LocalDateTime.of(2023, 9, 20, 19, 0));

        List<RestaurantReservation> reservations = Arrays.asList(reservation1, reservation2);

        when(restaurantReservationRepositoryMocked.findByUserIdOrderByReservationDateReservationDateAsc(null))
                .thenReturn(reservations);

        List<RestaurantReservation> result = restaurantReservationRepositoryMocked.findByUserIdOrderByReservationDateReservationDateAsc(null);

        assertEquals(2, result.size(), "Expected 2 reservations to be returned.");
        assertEquals(LocalDate.of(2023, 9, 15), result.get(0).getReservationDate());
        assertEquals(LocalDate.of(2023, 9, 20), result.get(1).getReservationDate());
        verify(restaurantReservationRepositoryMocked, times(1)).findByUserIdOrderByReservationDateReservationDateAsc(null);
    }

    @Test
    public void testFindByUserIdOrderByReservationDateReservationDateAsc_MultipleUsers() {
        User firstUser = new User();
        firstUser.setId(1L);
        User secondUser = new User();
        secondUser.setId(2L);

        RestaurantReservation reservation1 = new RestaurantReservation();
        reservation1.setId(1L);
        reservation1.setUser(firstUser);
        reservation1.setReservationDate(LocalDate.of(2023, 9, 15));
        reservation1.setReservationTime(LocalDateTime.of(2023, 9, 15, 19, 0));

        RestaurantReservation reservation2 = new RestaurantReservation();
        reservation2.setId(2L);
        reservation2.setUser(secondUser);
        reservation2.setReservationDate(LocalDate.of(2023, 9, 20));
        reservation2.setReservationTime(LocalDateTime.of(2023, 9, 20, 19, 0));

        List<RestaurantReservation> reservations = List.of(reservation1);

        when(restaurantReservationRepositoryMocked.findByUserIdOrderByReservationDateReservationDateAsc(firstUser.getId()))
                .thenReturn(reservations);

        List<RestaurantReservation> result = restaurantReservationRepositoryMocked.findByUserIdOrderByReservationDateReservationDateAsc(firstUser.getId());

        assertEquals(1, result.size(), "Only reservations for first user should be returned.");
        assertEquals(LocalDate.of(2023, 9, 15), result.get(0).getReservationDate());
        verify(restaurantReservationRepositoryMocked, times(1)).findByUserIdOrderByReservationDateReservationDateAsc(firstUser.getId());
    }
}