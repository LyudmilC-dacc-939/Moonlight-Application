package com.moonlight.service.impl.bar;

import com.moonlight.advice.exception.InvalidDateRangeException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.bar.BarReservationRequest;
import com.moonlight.dto.bar.BarReservationResponse;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import com.moonlight.model.user.User;
import com.moonlight.repository.bar.BarReservationRepository;
import com.moonlight.repository.bar.SeatRepository;
import com.moonlight.service.BarReservationService;
import com.moonlight.service.impl.user.CurrentUserImpl;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BarReservationServiceImpl implements BarReservationService {
    @Autowired
    private BarReservationRepository barReservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private CurrentUserImpl currentUserImpl;

    @Override
    public BarReservationResponse createReservation(BarReservationRequest request, User user) {
        if (!currentUserImpl.isCurrentUserMatch(user)) {
            throw new RecordNotFoundException("This user is not authorized to proceed this operation");
        } else {
            // Find the screen by screenName
            Screen screen = findScreenByName(request.getScreenName());
            // Check if date is allowed
            if (request.getReservationDate().isBefore(LocalDate.now())) {
                throw new InvalidDateRangeException("Reservation date can't be in the past");
            }
            // Find the seats by seatNumbers and screen
            Set<Seat> seats = new HashSet<>();
            for (Integer seatNumber : request.getSeatNumbers()) {
                if (seatNumber > 21) {
                    throw new UnavailableResourceException("Invalid seat number, please select a seat between 1 and 21");
                }
                Seat seat = seatRepository.findByScreenAndSeatNumber(screen, seatNumber)
                        .orElseThrow(() -> new RecordNotFoundException("Seat not found for number " + seatNumber + " on screen " + screen.getCurrentScreenName()));

                // Check if the seat is already reserved on the requested date
                boolean seatTaken = barReservationRepository.existsBySeatAndReservationDate(seat, request.getReservationDate());
                if (seatTaken) {
                    throw new UnavailableResourceException("Seat " + seatNumber + " is already reserved for the selected date");
                }
                seats.add(seat);
            }


            double totalCost = 5 * seats.size();

            // Create and save the reservation
            BarReservation reservation = new BarReservation();
            reservation.setUser(user);
            reservation.setSeats(seats);
            reservation.setScreen(screen);
            reservation.setReservationDate(request.getReservationDate());
            reservation.setTotalCost(totalCost);

            BarReservation savedReservation = barReservationRepository.save(reservation);

            // Create and return the response
            Set<Integer> seatNumbers = seats.stream().map(Seat::getSeatNumber).collect(Collectors.toSet());

            return new BarReservationResponse(
                    savedReservation.getId(),
                    seatNumbers,
                    screen.getCurrentScreenName(),
                    totalCost,
                    savedReservation.getReservationDate()
            );
        }
    }

    private Screen findScreenByName(String screenName) {
        for (Screen screen : Screen.values()) {
            if (screen.getCurrentScreenName().equalsIgnoreCase(screenName)) {
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