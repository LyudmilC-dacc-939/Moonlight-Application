package com.moonlight.service.impl.bar;

import com.moonlight.advice.exception.ItemNotFoundException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.dto.bar.AddEventRequest;
import com.moonlight.dto.bar.AddEventResponse;
import com.moonlight.dto.bar.ScreenInformationResponse;
import com.moonlight.model.bar.Event;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import com.moonlight.repository.bar.EventRepository;
import com.moonlight.repository.bar.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BarServiceImplTest {
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private BarServiceImpl barService;
    private Set<Seat> seats;

    private AddEventRequest addEventRequest;
    private Event event;

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

        addEventRequest = new AddEventRequest();
        addEventRequest.setEventName("Champions League Football Match");
        addEventRequest.setEventDate(LocalDateTime.of(2024, 9, 30, 18, 0));
        addEventRequest.setScreenId(1);

        event = new Event();
        event.setId(1L);
        event.setEventName(addEventRequest.getEventName());
        event.setEventDate(addEventRequest.getEventDate());
        event.setScreens(Set.of(Screen.SCREEN_ONE));
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

    @Test
    void createEvent_SuccessfulCreation() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        AddEventResponse response = barService.createEvent(addEventRequest);

        assertNotNull(response);
        assertEquals("SCREEN: Football", response.getScreenDefaultName());  // assuming correct mapping

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void createEvent_InvalidScreenId_ShouldThrowItemNotFoundException() {
        addEventRequest.setScreenId(-1);

        Exception exception = assertThrows(ItemNotFoundException.class, () -> {
            barService.createEvent(addEventRequest);
        });

        assertEquals("Invalid screen id: -1", exception.getMessage());
    }

    @Test
    void createEvent_CorrectMappingBetweenRequestAndEvent() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        AddEventResponse response = barService.createEvent(addEventRequest);

        assertNotNull(response);
        assertEquals(addEventRequest.getEventName(), response.getEventName());
        assertEquals(addEventRequest.getEventDate(), response.getEventDate());
    }

    @Test
    void createEvent_VerifySavedEventName() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        AddEventResponse response = barService.createEvent(addEventRequest);

        assertEquals("Champions League Football Match", response.getEventName());
    }

    @Test
    void createEvent_VerifySavedEventDate() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        AddEventResponse response = barService.createEvent(addEventRequest);

        assertEquals(LocalDateTime.of(2024, 9, 30, 18, 0), response.getEventDate());
    }

    @Test
    void createEvent_VerifyScreenAssignment() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        AddEventResponse response = barService.createEvent(addEventRequest);

        Screen savedScreen = event.getScreens().iterator().next();
        assertEquals(savedScreen.getDefaultScreenName(), response.getScreenDefaultName());
    }

    @Test
    void searchByScreen_ShouldFindSeatsByCurrentScreenName() {
        Seat seat1 = mock(Seat.class);
        Screen screen1 = mock(Screen.class);
        when(screen1.getCurrentScreenName()).thenReturn("Football Event");
        when(seat1.getScreen()).thenReturn(screen1);

        Seat seat2 = mock(Seat.class);
        Screen screen2 = mock(Screen.class);
        when(screen2.getCurrentScreenName()).thenReturn(null);
        when(screen2.getDefaultScreenName()).thenReturn("Tennis Event");
        when(seat2.getScreen()).thenReturn(screen2);

        when(seatRepository.findAll()).thenReturn(List.of(seat1, seat2));

        Set<Seat> result = barService.searchByScreen("Football");

        assertEquals(1, result.size());
        assertEquals(seat1, result.iterator().next());
    }

    @Test
    void testFindByQuerySearch_found() {
        List<Event> events = List.of(new Event(1L, "Football Match", LocalDateTime.now(), Set.of(Screen.SCREEN_ONE)));
        when(eventRepository.findByEventIdOrEventNameOrEventDate(anyLong(), anyString(), any(LocalDate.class)))
                .thenReturn(events);

        List<Event> result = barService.findByQuerySearch(1L, "Football Match", LocalDate.now());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Football Match", result.get(0).getEventName());
    }

    @Test
    void testFindByQuerySearch_noEventsFound() {
        when(eventRepository.findByEventIdOrEventNameOrEventDate(anyLong(), anyString(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        assertThrows(RecordNotFoundException.class, () -> barService.findByQuerySearch(1L, "Nonexistent Event", LocalDate.now()));
    }

    @Test
    void testGetFullInfoForScreen_noEventsFound() {
        Screen screen = Screen.SCREEN_ONE;
        when(eventRepository.findAllByScreen(screen)).thenReturn(Collections.emptyList());

        ScreenInformationResponse response = barService.getFullInfoForScreen(screen);

        assertNotNull(response);
        assertEquals(screen, response.getScreen());
        assertTrue(response.getEventsForScreen().isEmpty());
    }

    @Test
    void testFindByQuerySearch_nullEventId_validEventName() {
        List<Event> events = List.of(new Event(1L, "Football Match", LocalDateTime.now(), Set.of(Screen.SCREEN_ONE)));
        when(eventRepository.findByEventIdOrEventNameOrEventDate(isNull(), eq("Football Match"), any(LocalDate.class)))
                .thenReturn(events);

        List<Event> result = barService.findByQuerySearch(null, "Football Match", LocalDate.now());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Football Match", result.get(0).getEventName());
    }

    @Test
    void testFindByQuerySearch_validEventId_nullEventName() {
        Event event = new Event(1L, "Football Match", LocalDateTime.now(), Set.of(Screen.SCREEN_ONE));
        when(eventRepository.findByEventIdOrEventNameOrEventDate(eq(1L), isNull(), any(LocalDate.class)))
                .thenReturn(List.of(event));

        List<Event> result = barService.findByQuerySearch(1L, null, LocalDate.now());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Football Match", result.get(0).getEventName());
    }

    @Test
    void testGetFullInfoForScreen_nullScreen() {
        assertThrows(NullPointerException.class, () -> barService.getFullInfoForScreen(null));
    }

    @Test
    void testGetFullInfoForScreen_noEventsForScreen() {
        Screen screen = Screen.SCREEN_ONE;
        when(eventRepository.findAllByScreen(screen)).thenReturn(Collections.emptyList());

        ScreenInformationResponse response = barService.getFullInfoForScreen(screen);

        assertNotNull(response);
        assertEquals(screen, response.getScreen());
        assertTrue(response.getEventsForScreen().isEmpty());
    }

    @Test
    void testGetFullInfoForScreen_multipleEventsForScreen() {
        Screen screen = Screen.SCREEN_ONE;
        List<Event> events = List.of(
                new Event(1L, "Football Match", LocalDateTime.now(), Set.of(Screen.SCREEN_ONE)),
                new Event(2L, "Football Final", LocalDateTime.now().plusDays(1), Set.of(Screen.SCREEN_ONE))
        );
        when(eventRepository.findAllByScreen(screen)).thenReturn(events);

        ScreenInformationResponse response = barService.getFullInfoForScreen(screen);

        assertNotNull(response);
        assertEquals(screen, response.getScreen());
        assertEquals(2, response.getEventsForScreen().size());
        assertEquals("Football Match", response.getEventsForScreen().get(0).getEventName());
        assertEquals("Football Final", response.getEventsForScreen().get(1).getEventName());
    }

}

