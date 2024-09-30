package com.moonlight.service;

import com.moonlight.dto.bar.AddEventRequest;
import com.moonlight.dto.bar.AddEventResponse;
import com.moonlight.dto.bar.ScreenInformationResponse;
import com.moonlight.model.bar.Event;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


public interface BarService {
    Set<Seat> searchByScreen(String screenName);

    Set<Seat> searchBySeatNumberAndByScreen(String screenName, Long seatNumber);

    AddEventResponse createEvent(AddEventRequest addEventRequest);

    List<Event> findByQuerySearch(Long eventId, String eventName, LocalDate eventDate);

    ScreenInformationResponse getFullInfoForScreen(Screen screen);
}
