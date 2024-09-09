package com.moonlight.service.impl.restaurant;

import com.moonlight.model.enums.RestaurantZone;
import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.repository.restaurant.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setTableNumber(5L);
        restaurant.setRestaurantZone(RestaurantZone.SALOON); // Setting the zone as enum
    }

    @Test
    void testFindByTableNumberOrZone() {
        Set<Restaurant> restaurantSet = new HashSet<>();
        restaurantSet.add(restaurant);

        when(restaurantRepository.findByTableNumberOrZone(5L, "SALOON"))
                .thenReturn(restaurantSet);

        Set<Restaurant> result = restaurantService.findByTableNumberOrZone(5L, "SALOON");

        assertEquals(1, result.size());  // Only one restaurant in mock result
        Restaurant foundRestaurant = result.iterator().next();
        assertEquals(restaurant.getTableNumber(), foundRestaurant.getTableNumber());
        assertEquals(RestaurantZone.SALOON, foundRestaurant.getRestaurantZone());

        verify(restaurantRepository, times(1))
                .findByTableNumberOrZone(5L, "SALOON");
    }

    @Test
    void testFindByTableNumberOrZone_whenNoTableNumber() {
        Set<Restaurant> restaurantSet = new HashSet<>();
        restaurantSet.add(restaurant);

        when(restaurantRepository.findByTableNumberOrZone(null, "SALOON"))
                .thenReturn(restaurantSet);

        Set<Restaurant> result = restaurantService.findByTableNumberOrZone(null, "SALOON");

        assertEquals(1, result.size());
        Restaurant foundRestaurant = result.iterator().next();
        assertEquals(RestaurantZone.SALOON, foundRestaurant.getRestaurantZone());

        verify(restaurantRepository, times(1))
                .findByTableNumberOrZone(null, "SALOON");
    }
}