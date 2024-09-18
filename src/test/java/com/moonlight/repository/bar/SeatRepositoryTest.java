package com.moonlight.repository.bar;

import com.moonlight.model.enums.Screen;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
public class SeatRepositoryTest {

    @Mock
    private SeatRepository seatRepository;

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
}
