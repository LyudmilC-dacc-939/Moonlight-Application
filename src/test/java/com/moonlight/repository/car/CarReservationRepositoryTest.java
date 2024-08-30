package com.moonlight.repository.car;

import com.moonlight.model.car.Car;
import com.moonlight.model.car.CarReservation;
import com.moonlight.model.enums.CarType;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
class CarReservationRepositoryTest {

    @Autowired
    private CarReservationRepository carReservationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Car car;
    private CarReservation reservation;

    @BeforeEach
    void setUp() {
        // Create and persist a User entity
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmailAddress("test@test.com");
        user.setPassword("password123");
        user.setPhoneNumber("123456789");
        user = entityManager.persistAndFlush(user);  // Persist and get managed entity with ID

        // Create and persist a Car entity
        car = new Car();
        car.setCarBrand("Toyota");
        car.setType(CarType.SPORT);
        car = entityManager.persistAndFlush(car);  // Persist and get managed entity with ID

        // Create a test CarReservation entity
        reservation = new CarReservation();
        reservation.setUser(user);
        reservation.setCar(car);
        reservation.setStartDate(LocalDate.now().plusDays(2));
        reservation.setEndDate(LocalDate.now().plusDays(4));
        reservation.setTotalCost(1000.0);
        reservation.setStatus(ReservationStatus.PENDING);

        // Save the CarReservation to the repository
        carReservationRepository.save(reservation);
    }

    @Test
    void testFindByUserId() {
        List<CarReservation> reservations = carReservationRepository.findByUserId(user.getId());
        assertFalse(reservations.isEmpty());
        assertEquals(1, reservations.size());
        assertEquals(user.getId(), reservations.get(0).getUser().getId());
    }

    @Test
    void testFindByCarAndStartDateEndDate() {
        List<CarReservation> reservations = carReservationRepository.findByCarAndStartDateEndDate(
                car.getId(), reservation.getStartDate(), reservation.getEndDate());

        assertFalse(reservations.isEmpty());
        assertEquals(1, reservations.size());
        assertEquals(car.getId(), reservations.get(0).getCar().getId());
    }

    @Test
    void testFindOverlappingReservations() {
        LocalDate newStartDate = LocalDate.now().plusDays(3);
        LocalDate newEndDate = LocalDate.now().plusDays(5);

        List<CarReservation> overlappingReservations = carReservationRepository.findOverlappingReservations(
                car.getId(), newStartDate, newEndDate);

        assertFalse(overlappingReservations.isEmpty());
        assertEquals(reservation.getId(), overlappingReservations.get(0).getId());
    }

    @Test
    void testFindReservedCarIdsByDateRange() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        List<Long> reservedCarIds = carReservationRepository.findReservedCarIdsByDateRange(startDate, endDate);

        assertFalse(reservedCarIds.isEmpty());
        assertEquals(1, reservedCarIds.size());
        assertEquals(car.getId(), reservedCarIds.get(0));
    }
}