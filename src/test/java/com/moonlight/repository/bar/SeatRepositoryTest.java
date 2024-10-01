package com.moonlight.repository.bar;

import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@Transactional
public class SeatRepositoryTest {

    @Mock
    private SeatRepository seatRepository;

    private Seat seat;

    @Test
    public void testExistsByScreenAndSeatNumber_Exists() {
        when(seatRepository.existsByScreenAndSeatNumber(Screen.SCREEN_ONE, 1)).thenReturn(true);

        boolean exists = seatRepository.existsByScreenAndSeatNumber(Screen.SCREEN_ONE, 1);

        assertTrue(exists);
        verify(seatRepository, times(1)).existsByScreenAndSeatNumber(Screen.SCREEN_ONE, 1);
    }

    @Test
    public void testExistsByScreenAndSeatNumber_NotExists() {
        // Mocking the repository response to return false
        when(seatRepository.existsByScreenAndSeatNumber(Screen.SCREEN_ONE, 999)).thenReturn(false);

        // When
        boolean exists = seatRepository.existsByScreenAndSeatNumber(Screen.SCREEN_ONE, 999);

        // Then
        assertFalse(exists);
        verify(seatRepository, times(1)).existsByScreenAndSeatNumber(Screen.SCREEN_ONE, 999);
    }

    @Test
    public void findByScreen() {
        Screen screen = Screen.SCREEN_ONE;

        Seat seat1 = new Seat();
        seat1.setId(1L);
        seat1.setSeatNumber(1);
        seat1.setScreen(Screen.SCREEN_ONE);

        List<Seat> allSeats = List.of(seat1);
        when(seatRepository.findByScreen(screen)).thenReturn(allSeats);

        List<Seat> actualSeats = seatRepository.findByScreen(screen);

        assertEquals(allSeats.size(), actualSeats.size());
        assertTrue(actualSeats.containsAll(allSeats));
    }
}
