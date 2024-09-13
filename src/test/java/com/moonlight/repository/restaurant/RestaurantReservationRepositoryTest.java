package com.moonlight.repository.restaurant;

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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RestaurantReservationRepositoryTest {

    @Autowired
    private RestaurantReservationRepository restaurantReservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;
    private User user;

    private Restaurant restaurant;

    private RestaurantReservation restaurantReservation;


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
        restaurantReservationRepository.save(restaurantReservation);


    }
    @Test
    void testFindByUserId() {
        List<RestaurantReservation> reservations = restaurantReservationRepository.findByUserId(user.getId());
        assertFalse(reservations.isEmpty());
        assertEquals(1,reservations.size());
        assertEquals(user.getId(),reservations.get(0).getUser().getId());
    }
    @Test
    public void testAlreadyExistingReservation_Exists_ReturnsOne() {
        int result = restaurantReservationRepository.alreadyExistingReservation(
                restaurant.getId(),
                restaurantReservation.getReservationDate(),
                restaurantReservation.getReservationTime(),
                restaurantReservation.getReservationEndTime(),
                restaurantReservation.getTableNumber());

        assertEquals(1,result);
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

        assertEquals(0,result);
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

        assertEquals(1,result);
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

        assertEquals(0,result);
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

        assertEquals(0,result);
    }
}