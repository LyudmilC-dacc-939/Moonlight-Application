package com.moonlight.repository.restaurant;

import com.moonlight.model.restaurant.RestaurantReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RestaurantReservationRepository extends JpaRepository<RestaurantReservation, Long> {
    List<RestaurantReservation> findByUserId(Long userId);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END " +
            "FROM restaurant_reservations r " +
            "WHERE r.restaurant_id = :restaurantId " +
            "AND r.reservation_date = :reservationDate " +
            "AND r.reservation_time = :reservationTime " +
            "AND r.table_number = :tableNumber " +
            "AND r.reservation_end_time = :reservationEndTime " +
            "OR (:reservationTime < r.reservation_end_time " +
            "AND r.table_number = :tableNumber " +
            "AND :reservationEndTime > r.reservation_time)", nativeQuery = true)
    int alreadyExistingReservation(
            @Param("restaurantId") Long restaurantId,
            @Param("reservationDate") LocalDate reservationDate,
            @Param("reservationTime") LocalDateTime reservationTime,
            @Param("reservationEndTime") LocalDateTime reservationEndTime,
            @Param("tableNumber") Long tableNumber
    );

    @Query(value = "SELECT * FROM restaurant_reservations r " +
            "WHERE :userId IS NULL OR r.user_id = :userId " +
            "ORDER BY r.reservation_date ASC", nativeQuery = true)
    List<RestaurantReservation> findByUserIdOrderByReservationDateReservationDateAsc(@Param("userId") Long userId);
}
