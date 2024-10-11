package com.moonlight.repository.bar;

import com.moonlight.model.bar.Bar;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.bar.Event;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.enums.Screen;
import com.moonlight.model.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private User user;

    @BeforeEach
    void setUp() {
        Event event1 = new Event();
        event1.setEventName("Football Match");
        event1.setEventDate(LocalDateTime.now().plusDays(1));
        event1.setScreens(Set.of(Screen.SCREEN_ONE));
        entityManager.persist(event1);

        Event event2 = new Event();
        event2.setEventName("Tennis Match");
        event2.setEventDate(LocalDateTime.now().plusDays(2));
        event2.setScreens(Set.of(Screen.SCREEN_TWO));
        entityManager.persist(event2);

        Event event3 = new Event();
        event3.setEventName("Basketball Match");
        event3.setEventDate(LocalDateTime.now().plusDays(3));
        event3.setScreens(Set.of(Screen.SCREEN_THREE));
        entityManager.persist(event3);

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmailAddress("test@test.com");
        user.setPassword("password123");
        user.setPhoneNumber("123456789");
        user = entityManager.merge(user);

        System.out.println("User ID after merge: " + user.getId());

        Bar bar = new Bar();
        bar.setBarName("Test Bar");
        bar.setScreens(new HashSet<>(Set.of(Screen.SCREEN_ONE, Screen.SCREEN_TWO, Screen.SCREEN_THREE)));
        entityManager.persist(bar);

        seat = new Seat();
        seat.setScreen(Screen.SCREEN_ONE);
        seat.setSeatNumber(1);
        seat.setBar(bar);
        entityManager.persist(seat);

        BarReservation reservation1 = new BarReservation();
        reservation1.setSeats(Set.of(seat));
        reservation1.setUser(user);
        reservation1.setScreen(Screen.SCREEN_ONE);
        reservation1.setStatus(ReservationStatus.PENDING);
        reservation1.setReservationDate(LocalDate.now().plusDays(1));
        reservation1.setEvent(event1);
        entityManager.persist(reservation1);

        BarReservation reservation2 = new BarReservation();
        reservation2.setUser(user);
        reservation2.setSeats(Set.of(seat));
        reservation2.setScreen(Screen.SCREEN_ONE);
        reservation2.setStatus(ReservationStatus.PENDING);
        reservation2.setReservationDate(LocalDate.now().plusDays(1));
        reservation2.setEvent(event2);
        entityManager.persist(reservation2);

        BarReservation reservation3 = new BarReservation();
        reservation3.setUser(user);
        reservation3.setSeats(Set.of(seat));
        reservation3.setScreen(Screen.SCREEN_ONE);
        reservation3.setStatus(ReservationStatus.PENDING);
        reservation3.setReservationDate(LocalDate.now().plusDays(2));
        reservation3.setEvent(event3);
        entityManager.persist(reservation3);

        entityManager.flush();
    }

    @Test
    void testExistsBySeatAndReservationDate() {
        boolean exists = barReservationRepository.existsBySeatAndReservationDate(seat, LocalDate.now().plusDays(1));
        assertThat(exists).isTrue();

        boolean notExists = barReservationRepository.existsBySeatAndReservationDate(seat, LocalDate.now());
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindByUserId() {
        List<BarReservation> reservations = barReservationRepository.findByUserId(user.getId());

        assertThat(reservations).hasSize(3);
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