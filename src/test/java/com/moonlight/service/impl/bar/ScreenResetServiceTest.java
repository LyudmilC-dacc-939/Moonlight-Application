package com.moonlight.service.impl.bar;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.moonlight.model.bar.Event;
import com.moonlight.model.enums.Screen;
import com.moonlight.repository.bar.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

class ScreenResetServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ScreenResetService screenResetService;

    private Event event;
    private Screen screen;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        screen = Screen.SCREEN_ONE;
        event = new Event();
        event.setEventName("Champions League Football Match");
        event.setEventDate(LocalDateTime.of(2024, 9, 30, 18, 0));
        event.setScreens(Set.of(screen));
    }

    @Test
    void resetScreenNames_ShouldResetToDefault() {
        LocalDateTime pastTime = LocalDateTime.now().minusHours(5);
        event.setEventDate(pastTime);
        when(eventRepository.findAllByEventDateBefore(any(LocalDateTime.class))).thenReturn(List.of(event));

        screenResetService.resetScreenNames();

        assertEquals("SCREEN: Football", screen.getCurrentScreenName());
    }

    @Test
    void updateScreenNamesForToday_ShouldSetScreenNameToEventName() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        event.setEventDate(LocalDateTime.now());
        when(eventRepository.findAllByEventDateBetween(startOfDay, endOfDay)).thenReturn(List.of(event));

        screenResetService.updateScreenNamesForToday();

        assertEquals("Champions League Football Match", screen.getCurrentScreenName());
    }

    @Test
    void resetScreenNames_NoEvents_ShouldDoNothing() {
        when(eventRepository.findAllByEventDateBefore(any(LocalDateTime.class))).thenReturn(List.of());


        screenResetService.resetScreenNames();

        verify(eventRepository, times(1)).findAllByEventDateBefore(any(LocalDateTime.class));
        assertEquals("SCREEN: Football", screen.getDefaultScreenName());
    }

    @Test
    void updateScreenNamesForToday_NoEvents_ShouldDoNothing() {
        when(eventRepository.findAllByEventDateBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of());

        screenResetService.updateScreenNamesForToday();

        verify(eventRepository, times(1)).findAllByEventDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
        assertEquals("SCREEN: Football", screen.getDefaultScreenName());
    }
}


