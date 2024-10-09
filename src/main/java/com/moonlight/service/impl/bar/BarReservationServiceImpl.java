package com.moonlight.service.impl.bar;

import com.moonlight.advice.exception.InvalidDateRangeException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.bar.BarReservationRequest;
import com.moonlight.dto.bar.BarReservationResponse;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.bar.Event;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.enums.Screen;
import com.moonlight.model.user.User;
import com.moonlight.repository.bar.BarReservationRepository;
import com.moonlight.repository.bar.EventRepository;
import com.moonlight.repository.bar.SeatRepository;
import com.moonlight.service.BarReservationService;
import com.moonlight.service.impl.user.CurrentUserImpl;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BarReservationServiceImpl implements BarReservationService {
    @Autowired
    private BarReservationRepository barReservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private CurrentUserImpl currentUserImpl;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ScreenResetService screenResetService;


    @Override
    public BarReservationResponse createReservation(BarReservationRequest request, User user) {
        if (!currentUserImpl.isCurrentUserMatch(user)) {
            throw new RecordNotFoundException("This user is not authorized to proceed this operation");
        } else {
            // Find the screen by screenName
            Screen screen = findScreenById(request.getScreenId());
            if (screen == null) {
                throw new RecordNotFoundException("Screen not found");
            }
            Event event = findEventById(request.getEventId());
            if (event == null) {
                throw new RecordNotFoundException("An event must be selected for the reservation");
            }
            if (!event.getScreens().contains(screen)) {
                throw new RecordNotFoundException("The selected screen does not match the event screen");
            }
            if (!request.getReservationDate().isEqual(event.getEventDate().toLocalDate())) {
                throw new InvalidDateRangeException("Reservation date must match event date");
            }
            // Check if date is allowed
            if (request.getReservationDate().isBefore(LocalDate.now())) {
                throw new InvalidDateRangeException("Reservation date can't be in the past");
            }
            // Find the seats by seatNumbers and screen
            Set<Seat> seats = new HashSet<>();
            List<Integer> alreadyReservedSeats = new ArrayList<>();
            for (Integer seatNumber : request.getSeatNumbers()) {
                if (seatNumber > 21 || seatNumber < 1) {
                    throw new UnavailableResourceException("Invalid seat number, please select a seat between 1 and 21");
                }
                Seat seat = seatRepository.findByScreenAndSeatNumber(screen, seatNumber)
                        .orElseThrow(() -> new RecordNotFoundException("Seat not found for number " + seatNumber + " on screen " + screen.getCurrentScreenName()));

                // Check if the seat is already reserved on the requested date
                boolean seatTaken = barReservationRepository.existsBySeatAndReservationDate(seat, request.getReservationDate());
                if (seatTaken) {
                    alreadyReservedSeats.add(seatNumber);
                }
            }
            if (!alreadyReservedSeats.isEmpty()) {
                throw new UnavailableResourceException("Seat " + alreadyReservedSeats + " is already reserved for the selected date");
            }

            for (Integer seatNumber : request.getSeatNumbers()) {
                Seat seat = seatRepository.findByScreenAndSeatNumber(screen, seatNumber)
                        .orElseThrow(() -> new RecordNotFoundException("Seat not found for number " + seatNumber + " on screen " + screen.getCurrentScreenName()));
                seats.add(seat);
            }

            double totalCost = 5 * seats.size();

            // Create and save the reservation
            BarReservation reservation = new BarReservation();
            reservation.setUser(user);
            reservation.setSeats(seats);
            reservation.setScreen(screen);
            reservation.setEvent(event);
            reservation.setReservationDate(request.getReservationDate());
            reservation.setTotalCost(totalCost);
            reservation.setStatus(ReservationStatus.PENDING);

            BarReservation savedReservation = barReservationRepository.save(reservation);

            // Create and return the response
            Set<Integer> seatNumbers = seats.stream().map(Seat::getSeatNumber).collect(Collectors.toSet());


            return new BarReservationResponse(
                    savedReservation.getUser().getId(),
                    savedReservation.getId(),
                    seatNumbers,
                    (long) screen.getId(),
                    event.getEventName(),
                    totalCost,
                    savedReservation.getReservationDate(),
                    user.getFirstName()
            );
        }
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RecordNotFoundException("Event with ID " + eventId + " not found"));
    }


    private Screen findScreenById(int screenId) {
        for (Screen screen : Screen.values()) {
            if (screen.getId() == screenId) {
                return screen;
            }
        }
        throw new RecordNotFoundException("Screen with ID" + screenId + " not found");
    }

    private Screen findScreenByName(String screenName) {
        for (Screen screen : Screen.values()) {
            if (screen.getDefaultScreenName().equalsIgnoreCase(screenName)) {
                return screen;
            }
        }
        throw new RecordNotFoundException("Invalid screen name: " + screenName);
    }

    @Override
    public List<BarReservation> getBarReservationsByUserId(Long userId) {
        return barReservationRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    @SneakyThrows
    public List<Seat> getAvailableSeats(String screenName, LocalDate reservationDate) {

        Screen screen = findScreenByName(screenName);

        List<Seat> allSeats = seatRepository.findByScreen(screen);

        List<BarReservation> reservations = barReservationRepository.findByScreenAndReservationDate(
                screen, reservationDate);

        Set<Seat> reservedSeats = reservations.stream()
                .flatMap(reservation -> reservation.getSeats().stream())
                .collect(Collectors.toSet());

        List<Seat> availableSeats = allSeats.stream()
                .filter(seat -> !reservedSeats.contains(seat))
                .toList();

        return availableSeats;
    }
}