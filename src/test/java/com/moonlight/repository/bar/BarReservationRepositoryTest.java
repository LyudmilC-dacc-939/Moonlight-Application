package com.moonlight.repository.bar;

import com.moonlight.model.bar.Bar;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import com.moonlight.model.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BarReservationRepositoryTest {

    @Autowired
    private BarReservationRepository barReservationRepository;

    @Autowired
    private EntityManager entityManager;

    private Seat seat;
    private Bar bar;
    private User user;

    @BeforeEach
    void setUp() {
        // Create and persist a User
        user = new User();
        user.setId(1L); // Set an ID or any other necessary properties
        user.setId(3L); // Set an ID or any other necessary properties
        user.setId(1L); // Set an ID or any other necessary properties
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmailAddress("test@test.com");
        user.setPassword("password123");
        user.setPhoneNumber("123456789");

        // Set other user properties as necessary
        user = entityManager.merge(user);

        // Create and persist a Bar
        Bar bar = new Bar();
        bar.setBarName("Test Bar");
        bar.setScreens(new HashSet<>(Set.of(Screen.SCREEN_ONE, Screen.SCREEN_TWO, Screen.SCREEN_THREE))); // Assuming this is how you initialize screens
        entityManager.persist(bar);

        // Create and persist a Seat
        seat = new Seat();
        seat.setScreen(Screen.SCREEN_ONE);
        seat.setSeatNumber(1);
        seat.setBar(bar); // Set the Bar reference
        entityManager.persist(seat);

        // Persist the seat to the database (Assuming you have a SeatRepository or similar)
        entityManager.persist(seat);

        // Create and persist a BarReservation
        BarReservation reservation = new BarReservation();
        reservation.setSeats(new HashSet<Seat>() {{
            add(seat);
        }});
        reservation.setUser(user);
        reservation.setScreen(Screen.SCREEN_ONE);
        reservation.setReservationDate(LocalDate.now().plusDays(1)); // Future date

        // Persist the reservation
        barReservationRepository.save(reservation);

        // Create and persist some reservations with the user
        BarReservation reservation1 = new BarReservation();
        reservation1.setUser(user); // Associate the reservation with the user
        reservation1.setSeats(new HashSet<Seat>() {{
            add(seat);
        }});
        reservation1.setScreen(Screen.SCREEN_ONE);
        reservation1.setReservationDate(LocalDate.now().plusDays(1)); // Example date
        entityManager.persist(reservation1);

        BarReservation reservation2 = new BarReservation();
        reservation2.setUser(user); // Associate the reservation with the user
        reservation2.setSeats(new HashSet<Seat>() {{
            add(seat);
        }});
        reservation2.setScreen(Screen.SCREEN_ONE);
        reservation2.setReservationDate(LocalDate.now().plusDays(2)); // Example date
        entityManager.persist(reservation2);
    }

    @Test
    void testExistsBySeatAndReservationDate() {
        // Check that the reservation exists for the seat and date
        boolean exists = barReservationRepository.existsBySeatAndReservationDate(seat, LocalDate.now().plusDays(1));
        assertThat(exists).isTrue(); // Should return true since we added a reservation

        // Check for a date that doesn't exist
        boolean notExists = barReservationRepository.existsBySeatAndReservationDate(seat, LocalDate.now());
        assertThat(notExists).isFalse(); // Should return false since there is no reservation for today
    }

    @Test
    void testFindByUserId() {
        List<BarReservation> reservations = barReservationRepository.findByUserId(1L);

        assertThat(reservations).hasSize(3); // Expecting 2 reservations for user with ID 1
        assertThat(reservations).extracting("reservationDate").containsExactly(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );
    }

    @Test
    void findByScreenAndReservationDate() {
        int expectedReservations = 2;
        LocalDate reservationDate = LocalDate.now().plusDays(1);

        List<BarReservation> reservations = barReservationRepository
                .findByScreenAndReservationDate(Screen.SCREEN_ONE, reservationDate);
        assertThat(reservations).hasSize(expectedReservations);
        assertThat(reservations).extracting(BarReservation::getReservationDate)
                .containsExactly(reservationDate, reservationDate);
        assertThat(reservations).extracting(BarReservation::getScreen)
                .containsOnly(Screen.SCREEN_ONE);
    }
}