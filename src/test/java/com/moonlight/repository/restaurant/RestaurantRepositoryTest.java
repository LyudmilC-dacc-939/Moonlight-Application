package com.moonlight.repository.restaurant;

import com.moonlight.model.enums.RestaurantZone;
import com.moonlight.model.restaurant.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RestaurantRepositoryTest {


    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        // Method one
        Restaurant newTable = new Restaurant();
        newTable.setRestaurantZone(RestaurantZone.SALOON);
        newTable.setTableNumber(10L);
        newTable.setMaxNumberOfSeats(4);

        restaurantRepository.save(newTable);

        // Method Two
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setTableNumber(1L);
        restaurant1.setRestaurantZone(RestaurantZone.SALOON);
        restaurant1.setMaxNumberOfSeats(4);

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setTableNumber(2L);
        restaurant2.setRestaurantZone(RestaurantZone.BAR);
        restaurant2.setMaxNumberOfSeats(1);

        Restaurant restaurant3 = new Restaurant();
        restaurant3.setTableNumber(3L);
        restaurant3.setRestaurantZone(RestaurantZone.SALOON);
        restaurant3.setMaxNumberOfSeats(4);

        restaurantRepository.save(restaurant1);
        restaurantRepository.save(restaurant2);
        restaurantRepository.save(restaurant3);

    }

    @Test
    void testFindByTableNumber() {
        Optional<Restaurant> foundTable = restaurantRepository.findByTableNumber(10L);

        assertThat(foundTable).isPresent();
        assertThat(foundTable.get().getRestaurantZone().name()).isEqualTo(RestaurantZone.SALOON.toString());
        assertThat(foundTable.get().getTableNumber()).isEqualTo(10L);
        assertThat(foundTable.get().getMaxNumberOfSeats()).isEqualTo(4);

    }

    @Test
    void testFindByTableNumberOrZone_whenTableNumberMatches() {
        Set<Restaurant> result = restaurantRepository.findByTableNumberOrZone(1L, null);

        assertEquals(1, result.size());
        assertEquals(1L, result.iterator().next().getTableNumber());
    }

    @Test
    void testFindByTableNumberOrZone_whenZoneMatches() {
        Set<Restaurant> result = restaurantRepository.findByTableNumberOrZone(null, "SALOON");

        assertFalse(result.isEmpty());
        result.forEach(restaurant -> assertEquals(RestaurantZone.SALOON, restaurant.getRestaurantZone()));
    }

    @Test
    void testFindByTableNumberOrZone_whenBothParametersMatch() {
        Set<Restaurant> result = restaurantRepository.findByTableNumberOrZone(3L, RestaurantZone.SALOON.toString());

        assertEquals(1, result.size());
        assertEquals(3L, result.iterator().next().getTableNumber());
        assertEquals(RestaurantZone.SALOON, result.iterator().next().getRestaurantZone());
    }

    @Test
    void testFindByTableNumberOrZone_whenNoParametersMatch() {
        Set<Restaurant> result = restaurantRepository.findByTableNumberOrZone(99L, RestaurantZone.TERRACE.toString());

        assertTrue(result.isEmpty());
    }


}