package com.moonlight.repository.restaurant;

import com.moonlight.model.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByTableNumber(Long tableNumber);

    @Query(value = "SELECT * FROM restaurants r WHERE " +
            "(:tableNumber IS NULL OR r.table_number = :tableNumber) AND " +
            "(:restaurantZone IS NULL OR r.restaurant_zone = :restaurantZone)", nativeQuery = true)
    Set<Restaurant> findByTableNumberOrZone(@Param("tableNumber")Long tableNumber,
                                            @Param("restaurantZone")String restaurantZone);
}
