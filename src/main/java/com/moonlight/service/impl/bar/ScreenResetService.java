package com.moonlight.service.impl.bar;

import com.moonlight.model.bar.Event;
import com.moonlight.model.enums.Screen;
import com.moonlight.repository.bar.EventRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ScreenResetService {

    private final EventRepository eventRepository;

    public ScreenResetService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    public void onStartup() {
        resetScreenNames();
        updateScreenNamesForToday();
    }

    @Scheduled(cron = "0 0 0/6 * * ?")
    public void updateScreenNamesForToday() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        List<Event> eventsToday = eventRepository.findAllByEventDateBetween(startOfDay, endOfDay);

        for (Event event : eventsToday) {
            Set<Screen> screens = event.getScreens();
            for (Screen screen : screens) {
                screen.setScreenNameForEvent(event.getEventName());
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetScreenNames() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime cutoffTime = now.minusHours(4);

        List<Event> pastEvents = eventRepository.findAllByEventDateBefore(cutoffTime);

        for (Event event : pastEvents) {
            Set<Screen> screens = event.getScreens();
            for (Screen screen : screens) {
                screen.resetToDefault();
            }
        }
    }
}