package com.moonlight.service.impl.bar;

import com.moonlight.advice.exception.IllegalCurrentStateException;
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

        LocalDateTime now = LocalDateTime.now();
        addEventRequest = new AddEventRequest();
        addEventRequest.setEventName("Champions League Football Match");
        addEventRequest.setEventDate(now.plusDays(2));
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

        assertEquals("Unknown Screen ID: -1", exception.getMessage());
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

        assertEquals(addEventRequest.getEventDate(), response.getEventDate());
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

    @Test
    void testCreateEvent_EventDateInPast_ThrowsIllegalCurrentStateException() {
        addEventRequest.setEventDate(LocalDateTime.now().minusDays(1));

        IllegalCurrentStateException exception = assertThrows(
                IllegalCurrentStateException.class,
                () -> barService.createEvent(addEventRequest)
        );

        assertEquals("Event date must be in the future.", exception.getMessage());
    }

    @Test
    void testCreateEvent_InvalidScreenId_ThrowsItemNotFoundException() {
        addEventRequest.setScreenId(999);
        addEventRequest.setEventDate(LocalDateTime.now().plusDays(1));

        lenient().when(eventRepository.findByEventNameAndEventDateAndScreens(anyString(), any(), any()))
                .thenReturn(Collections.emptyList());

        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> barService.createEvent(addEventRequest)
        );

        assertEquals("Unknown Screen ID: 999", exception.getMessage());
    }

    @Test
    void testCreateEvent_ExistingEventWithSameNameAndDate_ThrowsIllegalCurrentStateException() {
        when(eventRepository.findByEventNameAndEventDateAndScreens(
                addEventRequest.getEventName(),
                addEventRequest.getEventDate(),
                Screen.fromId(addEventRequest.getScreenId()))
        ).thenReturn(Collections.singletonList(event));

        IllegalCurrentStateException exception = assertThrows(
                IllegalCurrentStateException.class,
                () -> barService.createEvent(addEventRequest)
        );

        assertEquals("An event with the same name is already scheduled on this date for the specified screen.",
                exception.getMessage());
    }

    @Test
    void testDoesFutureEventExist_NoMatchingEvents() {
        String inputName = "Some Event";
        LocalDateTime eventDate = LocalDateTime.now().plusDays(1);
        when(eventRepository.findByEventNameContainingIgnoreCaseAndEventDate(any(String.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(eventRepository.findByEventNameContainingIgnoreCaseAndEventDate(null, eventDate))
                .thenReturn(Collections.emptyList());

        boolean result = barService.doesFutureEventExist(inputName, eventDate);

        assertFalse(result, "Expected no matching future events.");
    }

    @Test
    void testDoesFutureEventExist_WithMatchingEvents() {
        String inputName = "Some Event";
        LocalDateTime eventDate = LocalDateTime.now().plusDays(1);

        Event existingEvent = new Event();
        existingEvent.setEventName("Some Future Event");

        when(eventRepository.findByEventNameContainingIgnoreCaseAndEventDate(any(String.class), any(LocalDateTime.class)))
                .thenReturn(List.of(existingEvent));
        when(eventRepository.findByEventNameContainingIgnoreCaseAndEventDate(null, eventDate))
                .thenReturn(Collections.emptyList());

        boolean result = barService.doesFutureEventExist(inputName, eventDate);

        assertTrue(result, "Expected to find a matching future event.");
    }

    @Test
    void testDoesFutureEventExist_WhenInputNameContainsFutureEventName_ReturnsTrue() {
        String inputName = "Some Future Event";
        LocalDateTime eventDate = LocalDateTime.now().plusDays(1);

        Event futureEvent = new Event();
        futureEvent.setEventName("Future Event");

        List<Event> futureEvents = Collections.singletonList(futureEvent);
        lenient().when(eventRepository.findByEventNameContainingIgnoreCaseAndEventDate(null, eventDate))
                .thenReturn(futureEvents);

        boolean result = barService.doesFutureEventExist(inputName, eventDate);

        assertTrue(result, "Expected doesFutureEventExist to return true when inputName contains a future event name.");
    }

    @Test
    void testDoesFutureEventExist_WhenInputNameDoesNotContainFutureEventName_ReturnsFalse() {
        String inputName = "Some Other Event";
        LocalDateTime eventDate = LocalDateTime.now().plusDays(1);

        Event futureEvent = new Event();
        futureEvent.setEventName("Different Future Event");

        List<Event> futureEvents = Collections.singletonList(futureEvent);
        lenient().when(eventRepository.findByEventNameContainingIgnoreCaseAndEventDate(null, eventDate))
                .thenReturn(futureEvents);

        boolean result = barService.doesFutureEventExist(inputName, eventDate);

        assertFalse(result,
                "Expected doesFutureEventExist to return false when inputName does not contain a future event name.");
    }

    @Test
    void testCreateEvent_WhenEventDateIsInThePast_ThrowsException() {
        AddEventRequest addEventRequest = new AddEventRequest();
        addEventRequest.setEventName("Past Event");
        addEventRequest.setEventDate(LocalDateTime.now().minusDays(1));

        IllegalCurrentStateException exception = assertThrows(
                IllegalCurrentStateException.class,
                () -> barService.createEvent(addEventRequest)
        );

        assertEquals("Event date must be in the future.", exception.getMessage());
    }

    @Test
    void testCreateEvent_WhenFutureEventExistsWithSimilarName_ThrowsException() {
        AddEventRequest addEventRequest = new AddEventRequest();
        addEventRequest.setEventName("Some Event");
        addEventRequest.setEventDate(LocalDateTime.now().plusDays(1));
        addEventRequest.setScreenId(1);

        Event existingEvent = new Event();
        existingEvent.setEventName("Some Event");

        List<Event> futureEvents = Collections.singletonList(existingEvent);

        lenient().when(eventRepository.findByEventNameContainingIgnoreCaseAndEventDate(
                        anyString(), any(LocalDateTime.class)))
                .thenReturn(futureEvents);

        IllegalCurrentStateException exception = assertThrows(
                IllegalCurrentStateException.class,
                () -> barService.createEvent(addEventRequest)
        );

        assertEquals("An event with a similar name already exists for future events.", exception.getMessage());
    }

    @Test
    void testCreateEvent_WhenScreenAlreadyHasEventOnSameDay_ThrowsException() {
        AddEventRequest addEventRequest = new AddEventRequest();
        addEventRequest.setEventName("Screen Event");
        addEventRequest.setEventDate(LocalDateTime.now().plusDays(1));
        addEventRequest.setScreenId(1);

        Screen screen = Screen.fromId(1);
        when(eventRepository.findByScreenAndEventDate(screen, addEventRequest.getEventDate().toLocalDate()))
                .thenReturn(Collections.singletonList(new Event()));

        IllegalCurrentStateException exception = assertThrows(
                IllegalCurrentStateException.class,
                () -> barService.createEvent(addEventRequest)
        );

        assertEquals("This screen already has an event scheduled on the same day.", exception.getMessage());
    }

    @Test
    void testCreateEvent_WhenExistingEventWithSameNameAndDateForScreen_ThrowsException() {
        AddEventRequest addEventRequest = new AddEventRequest();
        addEventRequest.setEventName("Event Name");
        addEventRequest.setEventDate(LocalDateTime.now().plusDays(1));
        addEventRequest.setScreenId(1);

        Screen screen = Screen.fromId(1);
        when(eventRepository.findByEventNameAndEventDateAndScreens("Event Name", addEventRequest.getEventDate(), screen))
                .thenReturn(Collections.singletonList(new Event()));

        IllegalCurrentStateException exception = assertThrows(
                IllegalCurrentStateException.class,
                () -> barService.createEvent(addEventRequest)
        );

        assertEquals("An event with the same name is already scheduled on this date for the specified screen.",
                exception.getMessage());
    }

    @Test
    void testCreateEvent_WhenExistingFutureEventWithSameName_ThrowsException() {
        AddEventRequest addEventRequest = new AddEventRequest();
        addEventRequest.setEventName("Another Future Event");
        addEventRequest.setEventDate(LocalDateTime.now().plusDays(1));

        lenient().when(eventRepository.findByEventNameAndEventDateAfter("Another Future Event", LocalDateTime.now()))
                .thenReturn(Collections.singletonList(new Event()));

        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> barService.createEvent(addEventRequest)
        );

        assertEquals("Unknown Screen ID: 0", exception.getMessage());
    }

    @Test
    void testCreateEvent_WhenExistingEventsWithSameName_ThrowsException() {
        AddEventRequest addEventRequest = new AddEventRequest();
        addEventRequest.setEventName("Some Event");
        addEventRequest.setEventDate(LocalDateTime.now().plusDays(1));
        addEventRequest.setScreenId(1);

        Event existingEvent = new Event();
        existingEvent.setEventName("Some Event");
        List<Event> existingEventsWithSameName = Collections.singletonList(existingEvent);

        when(eventRepository.findByEventNameAndEventDateAfter(eq("Some Event"), any(LocalDateTime.class)))
                .thenReturn(existingEventsWithSameName);

        IllegalCurrentStateException exception = assertThrows(
                IllegalCurrentStateException.class,
                () -> barService.createEvent(addEventRequest)
        );

        assertEquals("An event with the same name already exists for future events.", exception.getMessage());
    }
}

