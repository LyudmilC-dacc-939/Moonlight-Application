package com.moonlight.repository.bar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.moonlight.model.bar.Event;
import com.moonlight.model.enums.Screen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@DataJpaTest
@ActiveProfiles("test")
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Mock
    private EventRepository eventRepositoryMocked;


    @BeforeEach
    public void setUp() {

        Event event1 = new Event();
        event1.setEventName("Football Match");
        event1.setEventDate(LocalDateTime.now().plusDays(1));
        event1.setScreens(Set.of(Screen.SCREEN_ONE));

        Event event2 = new Event();
        event2.setEventName("Tennis Match");
        event2.setEventDate(LocalDateTime.now().plusDays(2));
        event2.setScreens(Set.of(Screen.SCREEN_TWO));

        eventRepository.save(event1);
        eventRepository.save(event2);
    }

    @Test
    void testFindAllByEventDateBefore() {
        LocalDateTime now = LocalDateTime.now();

        Event mockEvent = new Event();
        mockEvent.setEventName("Formula 1 Race");
        mockEvent.setEventDate(now.minusDays(1));

        when(eventRepositoryMocked.findAllByEventDateBefore(now))
                .thenReturn(Collections.singletonList(mockEvent));

        List<Event> result = eventRepositoryMocked.findAllByEventDateBefore(now);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Formula 1 Race", result.get(0).getEventName());
    }

    @Test
    void testFindAllByEventDateBetween() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(3);

        List<Event> result = eventRepository.findAllByEventDateBetween(startOfDay, endOfDay);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindByEventIdOrEventNameOrEventDate_validId() {
        Long eventId = 1L;

        Event mockEvent = new Event();
        mockEvent.setId(eventId);
        mockEvent.setEventName("Football Match");
        mockEvent.setEventDate(LocalDate.now().plusDays(1).atStartOfDay());

        when(eventRepositoryMocked.findByEventIdOrEventNameOrEventDate(eventId, null, null))
                .thenReturn(Collections.singletonList(mockEvent));

        List<Event> result = eventRepositoryMocked.findByEventIdOrEventNameOrEventDate(eventId, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Football Match", result.get(0).getEventName());
    }

    @Test
    void testFindByEventIdOrEventNameOrEventDate_validEventName() {
        String eventName = "Tennis Match";

        Event mockEvent = new Event();
        mockEvent.setEventName(eventName);
        mockEvent.setEventDate(LocalDate.now().plusDays(1).atStartOfDay());

        when(eventRepositoryMocked.findByEventIdOrEventNameOrEventDate(null, eventName, null))
                .thenReturn(Collections.singletonList(mockEvent));

        List<Event> result = eventRepositoryMocked.findByEventIdOrEventNameOrEventDate(null, eventName, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tennis Match", result.get(0).getEventName());
    }

    @Test
    void testFindByEventIdOrEventNameOrEventDate_validEventDate() {
        LocalDate eventDate = LocalDate.now().plusDays(1);

        Event mockEvent = new Event();
        mockEvent.setEventName("Football Match");
        mockEvent.setEventDate(eventDate.atStartOfDay());

        when(eventRepositoryMocked.findByEventIdOrEventNameOrEventDate(null, null, eventDate))
                .thenReturn(Collections.singletonList(mockEvent));

        List<Event> result = eventRepositoryMocked.findByEventIdOrEventNameOrEventDate(null, null, eventDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Football Match", result.get(0).getEventName());
    }

    @Test
    void testFindByEventIdOrEventNameOrEventDate_noResults() {
        List<Event> result = eventRepositoryMocked.findByEventIdOrEventNameOrEventDate(99L, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllByScreen_validScreen() {
        Screen screen = Screen.SCREEN_ONE;

        List<Event> result = eventRepository.findAllByScreen(screen);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Football Match", result.get(0).getEventName());
    }

    @Test
    void testFindAllByScreen_noEvents() {
        Screen screen = Screen.SCREEN_THREE;

        List<Event> result = eventRepository.findAllByScreen(screen);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
