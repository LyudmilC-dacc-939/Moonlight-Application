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
import com.moonlight.service.BarService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BarServiceImpl implements BarService {

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private EventRepository eventRepository;


    @Override
    public Set<Seat> searchByScreen(String screenName) {
        List<Seat> allSeats = seatRepository.findAll();
        Set<Seat> seats = new HashSet<>();
        for (Seat seat : allSeats) {
            if (seat.getScreen().getCurrentScreenName() != null) {
                if (seat.getScreen().getCurrentScreenName().toLowerCase().contains(screenName.toLowerCase())) {
                    seats.add(seat);
                }
            } else {
                if (seat.getScreen().getDefaultScreenName().toLowerCase().contains(screenName.toLowerCase())) {
                    seats.add(seat);
                }
            }

        }
        return seats;
    }

    @Override
    public Set<Seat> searchBySeatNumberAndByScreen(String screenName, Long seatNumber) {
        Set<Seat> allSeats = searchByScreen(screenName);
        Set<Seat> seats = new HashSet<>();
        for (Seat seat : allSeats) {
            if (seatNumber == seat.getSeatNumber()) {
                seats.add(seat);
            }
        }
        return seats;
    }

    @Override
    public AddEventResponse createEvent(AddEventRequest addEventRequest) {
        Event addedEvent = new Event();
        addedEvent.setEventName(addEventRequest.getEventName());
        addedEvent.setEventDate(addEventRequest.getEventDate());

        LocalDateTime now = LocalDateTime.now();

        if (addEventRequest.getEventDate().isBefore(now)) {
            throw new IllegalCurrentStateException("Event date must be in the future.");
        }

        Screen screen;
        try {
            screen = Screen.fromId(addEventRequest.getScreenId());
        } catch (IllegalArgumentException e) {
            throw new ItemNotFoundException("Unknown Screen ID: " + addEventRequest.getScreenId(), e);
        }

        List<Event> existingEventsWithSameNameAndDate = eventRepository.findByEventNameAndEventDateAndScreens(
                addEventRequest.getEventName(),
                addEventRequest.getEventDate(),
                screen
        );

        if (!existingEventsWithSameNameAndDate.isEmpty()) {
            throw new IllegalCurrentStateException(
                    "An event with the same name is already scheduled on this date for the specified screen.");
        }

        List<Event> existingEventsWithSameName =
                eventRepository.findByEventNameAndEventDateAfter(addEventRequest.getEventName(), now);
        if (!existingEventsWithSameName.isEmpty()) {
            throw new IllegalCurrentStateException("An event with the same name already exists for future events.");
        }

        addedEvent.setScreens(Set.of(screen));
        Event savedEvent = eventRepository.save(addedEvent);

        AddEventResponse savedEventResponse = new AddEventResponse();
        BeanUtils.copyProperties(savedEvent, savedEventResponse);

        Screen savedScreen = savedEvent.getScreens().iterator().next();
        savedEventResponse.setScreenDefaultName(savedScreen.getDefaultScreenName());

        return savedEventResponse;
    }

    @Override
    public List<Event> findByQuerySearch(Long eventId, String eventName, LocalDate eventDate) {
        List<Event> foundQuery = eventRepository.findByEventIdOrEventNameOrEventDate(eventId, eventName, eventDate);
        if (foundQuery.isEmpty()) {
            throw new RecordNotFoundException("No events found for the search criteria");
        }
        return foundQuery;
    }

    @Override
    public ScreenInformationResponse getFullInfoForScreen(Screen screen) {
        List<Event> events = eventRepository.findAllByScreen(screen);

        ScreenInformationResponse screenInformationResponse = new ScreenInformationResponse();
        screenInformationResponse.setScreen(screen);
        screenInformationResponse.setDefaultScreenName(screen.getDefaultScreenName());
        screenInformationResponse.setCurrentScreenName(screen.getCurrentScreenName());
        screenInformationResponse.setEventsForScreen(events);

        return screenInformationResponse;
    }
}
