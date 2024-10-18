package com.moonlight.service.impl.stripe;

import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.car.CarReservation;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.hotel.HotelRoomReservation;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;
import com.moonlight.repository.bar.BarReservationRepository;
import com.moonlight.repository.car.CarReservationRepository;
import com.moonlight.repository.hotel.HotelRoomReservationRepository;
import com.moonlight.repository.restaurant.RestaurantReservationRepository;
import com.moonlight.service.ReservationService;
import com.moonlight.service.impl.user.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private HotelRoomReservationRepository hotelRoomReservationRepository;
    @Autowired
    private CarReservationRepository carReservationRepository;
    @Autowired
    private BarReservationRepository barReservationRepository;
    @Autowired
    private RestaurantReservationRepository restaurantReservationRepository;
    @Autowired
    private UserServiceImpl userService;

    private final Map<String, Long> paymentTrackingMap = new HashMap<>();

    public void trackPayment(String paymentIntendId, Long reservationId) {
        paymentTrackingMap.put(paymentIntendId, reservationId);
    }

    public double calculateTotalPendingAmount(Long userId, String reservationType, Long reservationId) {
        User foundUser = userService.getUserById(userId);
        if (foundUser == null) {
            throw new RecordNotFoundException("User not found");
        }

        double totalAmount = 0;
        switch (reservationType.toLowerCase()) {
            case "hotel":
                HotelRoomReservation hotelRoom = hotelRoomReservationRepository
                        .findById(reservationId).orElseThrow(null);
                if (hotelRoom != null && hotelRoom.getStatus() == ReservationStatus.PENDING) {
                    totalAmount += hotelRoom.getTotalCost();
                }
                break;
            case "car":
                CarReservation car = carReservationRepository
                        .findById(reservationId).orElseThrow(null);
                if (car != null && car.getStatus() == ReservationStatus.PENDING) {
                    totalAmount += car.getTotalCost();
                }
                break;
            case "restaurant":
                RestaurantReservation restaurant = restaurantReservationRepository
                        .findById(reservationId).orElseThrow(null);
                if (restaurant != null && restaurant.getStatus() == ReservationStatus.PENDING) {
                    totalAmount += restaurant.getSeatCost();
                }
                break;
            case "bar":
                BarReservation bar = barReservationRepository
                        .findById(reservationId).orElseThrow(null);
                if (bar != null && bar.getStatus() == ReservationStatus.PENDING) {
                    totalAmount += bar.getTotalCost();
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid reservation type");
        }
        return totalAmount;
    }

    @Transactional
    public void updateReservationStatus(String reservationId, String reservationType) {
        if(reservationType == null || reservationType.isEmpty()){
          throw new IllegalArgumentException("Reservation type is null or empty");
        }
        switch (reservationType.toLowerCase()) {
            case "hotel":
                Optional<HotelRoomReservation> hotelRoomReservation =
                        Optional.ofNullable(hotelRoomReservationRepository.findById(Long.valueOf(reservationId))
                                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation")));
                if (hotelRoomReservation.isPresent()) {
                    hotelRoomReservation.get().setStatus(ReservationStatus.CONFIRMED);
                    hotelRoomReservationRepository.save(hotelRoomReservation.get());
                }
                break;
            case "car":
                Optional<CarReservation> carReservation =
                        Optional.ofNullable(carReservationRepository.findById(Long.valueOf(reservationId))
                                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation")));
                if (carReservation.isPresent()) {
                    carReservation.get().setStatus(ReservationStatus.CONFIRMED);
                    carReservationRepository.save(carReservation.get());
                }
                break;
            case "restaurant":
                Optional<RestaurantReservation> restaurantReservation =
                        Optional.ofNullable(restaurantReservationRepository.findById(Long.valueOf(reservationId))
                                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation")));
                if (restaurantReservation.isPresent()) {
                    restaurantReservation.get().setStatus(ReservationStatus.CONFIRMED);
                    restaurantReservationRepository.save(restaurantReservation.get());
                }
                break;
            case "bar":
                Optional<BarReservation> barReservation =
                        Optional.ofNullable(barReservationRepository.findById(Long.valueOf(reservationId))
                                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation")));
                if (barReservation.isPresent()) {
                    barReservation.get().setStatus(ReservationStatus.CONFIRMED);
                    barReservationRepository.save(barReservation.get());
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown reservation type: " + reservationType);

        }
    }
}