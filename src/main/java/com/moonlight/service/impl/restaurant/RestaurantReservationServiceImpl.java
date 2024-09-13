package com.moonlight.service.impl.restaurant;

import com.moonlight.advice.exception.InvalidDateRangeException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.restaurant.RestaurantReservationRequest;
import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;
import com.moonlight.repository.restaurant.RestaurantRepository;
import com.moonlight.repository.restaurant.RestaurantReservationRepository;
import com.moonlight.service.RestaurantReservationService;
import com.moonlight.service.impl.user.CurrentUserImpl;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RestaurantReservationServiceImpl implements RestaurantReservationService {
    @Autowired
    private RestaurantReservationRepository restaurantReservationRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private CurrentUserImpl currentUserImpl;


    @Override
    @SneakyThrows
    @Transactional
    public RestaurantReservation createReservation(RestaurantReservationRequest request, User user) {
        if (!currentUserImpl.isCurrentUserMatch(user)) {
            throw new RecordNotFoundException("This user is not authorize to proceed this operation");
        } else {
            Restaurant restaurantTable = restaurantRepository.findByTableNumber(request.getTableNumber())
                    .orElseThrow(() -> new RecordNotFoundException("Table not found"));

            if (request.getNumberOfPeople() > restaurantTable.getMaxNumberOfSeats()) {
                throw new UnavailableResourceException("This table is not suitable for the number of selected people.");
            }

            if (request.isSmoking() != restaurantTable.getRestaurantZone().isSmokerArea()) {
                throw new UnavailableResourceException("This table isn't in smoking area");
            }

            double totalCost = restaurantTable.getRestaurantZone().getSeatPrice() * request.getNumberOfPeople();

            LocalDateTime reservationStartTime = LocalDateTime.of(request.getReservationDate(), request.getReservationTime());
            LocalDateTime reservationEndTime = reservationStartTime.plusHours(1);
            if (reservationStartTime.isBefore(LocalDateTime.now())) {
                throw new InvalidDateRangeException("Reservation date and time can't be in the past");
            }
            boolean reservationExists = restaurantReservationRepository.alreadyExistingReservation(
                    restaurantTable.getId(),
                    request.getReservationDate(),
                    reservationStartTime,
                    reservationEndTime,
                    request.getTableNumber()) == 1;
            if (reservationExists) {
                throw new InvalidDateRangeException("Reservation already exists for this table, try for different hour");
            }
            RestaurantReservation reservation = new RestaurantReservation();
            reservation.setUser(user);
            reservation.setRestaurant(restaurantTable);
            reservation.setReservationDate(request.getReservationDate());
            reservation.setReservationTime(reservationStartTime);
            reservation.setReservationEndTime(reservationEndTime);
            reservation.setZone(restaurantTable.getRestaurantZone());
            reservation.setSeatCost(totalCost);
            reservation.setTableNumber(request.getTableNumber());
            reservation.setSmoking(request.isSmoking());

            return restaurantReservationRepository.save(reservation);

        }
    }
}
