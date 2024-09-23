package com.moonlight.service.impl.bar;

import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import com.moonlight.repository.bar.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BarServiceImplTest {
    @Mock
    private SeatRepository seatRepository;
    @InjectMocks
    private BarServiceImpl barService;
    private Set<Seat> seats;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seats = new HashSet<>();

        Screen screenOne = Screen.SCREEN_ONE;
        Screen screenTwo = Screen.SCREEN_TWO;

        Seat seat1 = new Seat();
        seat1.setScreen(screenOne);
        seat1.setSeatNumber(1);

        Seat seat2 = new Seat();
        seat2.setScreen(screenTwo);
        seat2.setSeatNumber(2);

        seats.add(seat1);
        seats.add(seat2);
    }
    @Test
    void testSearchByScreen_Matches() {
        when(seatRepository.findAll()).thenReturn(new ArrayList<>(seats));

        Set<Seat> result = barService.searchByScreen("Football");

        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(seat -> seat.getScreen() == Screen.SCREEN_ONE));
    }
    @Test
    void testSearchByScreen_NoMatches() {
        when(seatRepository.findAll()).thenReturn(new ArrayList<>(seats));

        Set<Seat> result = barService.searchByScreen("AnimalPlanet");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchBySeatNumberAndByScreen_Matches() {
        when(seatRepository.findAll()).thenReturn(new ArrayList<>(seats));

        Set<Seat> result = barService.searchBySeatNumberAndByScreen("Football", 1L);

        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(seat -> seat.getScreen() == Screen.SCREEN_ONE));
        assertTrue(result.stream().anyMatch(seat -> seat.getSeatNumber() == 1));
    }
    @Test
    void testSearchBySeatNumberAndByScreen_NoMatches() {
        when(seatRepository.findAll()).thenReturn(new ArrayList<>(seats));

        Set<Seat> result = barService.searchBySeatNumberAndByScreen("Football", 200L);

        assertTrue(result.isEmpty());
    }
}
