package com.moonlight.service.payment;

import com.moonlight.advice.exception.InvalidInputException;
import com.moonlight.advice.exception.PayPalServiceException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.car.CarReservation;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.hotel.HotelRoomReservation;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.repository.bar.BarReservationRepository;
import com.moonlight.repository.car.CarReservationRepository;
import com.moonlight.repository.hotel.HotelRoomReservationRepository;
import com.moonlight.repository.restaurant.RestaurantReservationRepository;
import com.moonlight.service.PayService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private BarReservationRepository barReservationRepository;
    @Autowired
    private CarReservationRepository carReservationRepository;
    @Autowired
    private HotelRoomReservationRepository hotelRoomReservationRepository;
    @Autowired
    private RestaurantReservationRepository restaurantReservationRepository;
    @Autowired
    private PayPalService payPalService;

    @Override
    public String handleReservationPayment(Long userId, String reservationType, Long reservationId) throws InvalidInputException, RecordNotFoundException, PayPalServiceException {
        // Check if given reservationType is allowed
        if (!Arrays.asList("Bar", "Car", "HotelRoom", "Restaurant").contains(reservationType)) {
            throw new InvalidInputException("Invalid reservation type: " + reservationType);
        }

        // Find all reservation by userId
        List<?> reservations;
        String place;
        switch (reservationType) {
            case "Bar":
                reservations = barReservationRepository.findByUserId(userId);
                place = "Bar";
                break;
            case "Car":
                reservations = carReservationRepository.findByUserId(userId);
                place = "Car";
                break;
            case "HotelRoom":
                reservations = hotelRoomReservationRepository.findByUserId(userId);
                place = "HotelRoom";
                break;
            case "Restaurant":
                reservations = restaurantReservationRepository.findByUserId(userId);
                place = "Restaurant";
                break;
            default:
                throw new InvalidInputException("Unsupported reservation type.");
        }

        if (reservations.isEmpty()) {
            throw new RecordNotFoundException("No reservations were found.");
        }


        // Looks for the given reservationId in the loaded List of reservations
        Object reservation = reservations.stream()
                .filter(r -> getReservationId(r).equals(reservationId))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Reservation not found with ID: " + reservationId));

        // Initiating payment with the found order/data
        double totalAmount = getTotalAmount(reservation);
        String description = place + " Reservation payment for ID: " + reservationId;
        String cancelURL = "http://localhost:8080/api/v1/payments/paypal/cancel";
        String successUrl = "http://localhost:8080/api/v1/payments/paypal/success";

        try {
            Payment payment = payPalService.createPayment(
                    totalAmount,
                    "USD",
                    "paypal",
                    "sale",
                    description,
                    cancelURL,
                    successUrl
            );

            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return link.getHref();  // This is the redirect URL to PayPal
                }
            }
        } catch (PayPalRESTException e) {
            throw new PayPalServiceException("Error occurred while creating PayPal payment", e);
        }

        throw new PayPalServiceException("Failed to retrieve PayPal approval URL.");
    }

    @SneakyThrows
    private Long getReservationId(Object reservation) {
        if (reservation instanceof BarReservation) {
            return ((BarReservation) reservation).getId();
        } else if (reservation instanceof CarReservation) {
            return ((CarReservation) reservation).getId();
        } else if (reservation instanceof HotelRoomReservation) {
            return ((HotelRoomReservation) reservation).getId();
        } else if (reservation instanceof RestaurantReservation) {
            return ((RestaurantReservation) reservation).getId();
        }
        throw new InvalidInputException("Unsupported reservation type.");
    }

    @SneakyThrows
    private double getTotalAmount(Object reservation) {
        if (reservation instanceof BarReservation) {
            return ((BarReservation) reservation).getTotalCost();
        } else if (reservation instanceof CarReservation) {
            return ((CarReservation) reservation).getTotalCost();
        } else if (reservation instanceof HotelRoomReservation) {
            return ((HotelRoomReservation) reservation).getTotalCost();
        } else if (reservation instanceof RestaurantReservation) {
            return ((RestaurantReservation) reservation).getSeatCost();
        }
        throw new InvalidInputException("Unsupported reservation type.");
    }

    @Override
    public void confirmReservation(String reservationId, String reservationType) throws InvalidInputException {
        Long reservId = Long.parseLong(reservationId);
        switch (reservationType) {
            case "Bar":
                Optional<BarReservation> barReservation = barReservationRepository.findById(reservId);
                if (barReservation.isPresent()) {
                    BarReservation reservation = barReservation.get();
                    reservation.setStatus(ReservationStatus.CONFIRMED); // Set the new status
                    barReservationRepository.save(reservation); // Save the updated reservation
                } else {
                    throw new InvalidInputException("Bar reservation not found for ID: " + reservId);
                }
                break;
            case "Car":
                Optional<CarReservation> carReservation = carReservationRepository.findById(reservId);
                if (carReservation.isPresent()) {
                    CarReservation reservation = carReservation.get();
                    reservation.setStatus(ReservationStatus.CONFIRMED); // Set the new status
                    carReservationRepository.save(reservation); // Save the updated reservation
                } else {
                    throw new InvalidInputException("Bar reservation not found for ID: " + reservId);
                }
                break;
            case "HotelRoom":
                Optional<HotelRoomReservation> hotelReservation = hotelRoomReservationRepository.findById(reservId);
                if (hotelReservation.isPresent()) {
                    HotelRoomReservation reservation = hotelReservation.get();
                    reservation.setStatus(ReservationStatus.CONFIRMED); // Set the new status
                    hotelRoomReservationRepository.save(reservation); // Save the updated reservation
                } else {
                    throw new InvalidInputException("Bar reservation not found for ID: " + reservId);
                }
                break;
            case "Restaurant":
                Optional<RestaurantReservation> restaurantReservation = restaurantReservationRepository.findById(reservId);
                if (restaurantReservation.isPresent()) {
                    RestaurantReservation reservation = restaurantReservation.get();
                    reservation.setStatus(ReservationStatus.CONFIRMED); // Set the new status
                    restaurantReservationRepository.save(reservation); // Save the updated reservation
                } else {
                    throw new InvalidInputException("Bar reservation not found for ID: " + reservId);
                }
                break;
            default:
                throw new InvalidInputException("Unsupported reservation type.");
        }
    }
}
