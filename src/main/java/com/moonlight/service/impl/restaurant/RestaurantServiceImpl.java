package com.moonlight.service.impl.restaurant;

import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.repository.restaurant.RestaurantRepository;
import com.moonlight.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public Set<Restaurant> findByTableNumberOrZone(Long tableNumber, String restaurantZone) {
        return restaurantRepository.findByTableNumberOrZone(tableNumber, restaurantZone);
    }
}
