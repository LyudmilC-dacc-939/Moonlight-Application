package com.moonlight.service;

import com.moonlight.model.restaurant.Restaurant;

import java.util.Set;

public interface RestaurantService {
    Set<Restaurant> findByTableNumberOrZone(Long tableNumber,
                                            String restaurantZone);
}
