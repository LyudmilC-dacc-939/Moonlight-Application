package com.moonlight.repository.restaurant;

import com.moonlight.model.enums.RestaurantZone;
import com.moonlight.model.restaurant.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RestaurantRepositoryTest {


    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        Restaurant newTable = new Restaurant();
        newTable.setRestaurantZone(RestaurantZone.SALOON);
        newTable.setTableNumber(1L);
        newTable.setMaxNumberOfSeats(4);

        restaurantRepository.save(newTable);
    }

    @Test
    void testFindByTableNumber() {
        Optional<Restaurant> foundTable = restaurantRepository.findByTableNumber(1L);

        assertThat(foundTable).isPresent();
        assertThat(foundTable.get().getRestaurantZone().name()).isEqualTo("SALOON");
        assertThat(foundTable.get().getTableNumber()).isEqualTo(1L);
        assertThat(foundTable.get().getMaxNumberOfSeats()).isEqualTo(4);

    }

}