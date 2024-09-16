package com.moonlight.service;

import com.moonlight.dto.restaurant.RestaurantReservationRequest;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;

import java.util.List;

public interface RestaurantReservationService {
    RestaurantReservation createReservation(RestaurantReservationRequest request, User user);

    List<RestaurantReservation> getRestaurantReservationsByUserId(Long userId);
}
