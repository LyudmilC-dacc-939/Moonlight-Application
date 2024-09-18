package com.moonlight.service;

import com.moonlight.dto.restaurant.RestaurantReservationRequest;
import com.moonlight.dto.restaurant.TableAvailabilityResponse;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RestaurantReservationService {
    RestaurantReservation createReservation(RestaurantReservationRequest request, User user);


    TableAvailabilityResponse getAvailableTablesByDateAndPreferences
            (LocalDate reservationDate, LocalTime startTime
                    , LocalTime endTime, Integer seats, Boolean isSmoking);

    List<RestaurantReservation> getRestaurantReservationsByUserId(Long userId);
}

