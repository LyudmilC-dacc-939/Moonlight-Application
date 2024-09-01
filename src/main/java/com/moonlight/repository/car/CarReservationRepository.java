package com.moonlight.repository.car;

import com.moonlight.model.car.CarReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarReservationRepository extends JpaRepository<CarReservation, Long> {

    List<CarReservation> findByUserId(Long userId);

    @Query(value = "SELECT * FROM car_reservation cr WHERE " +
            "(:carId IS NULL OR cr.car_id LIKE %:carId%) AND " +
            "(:startDate IS NULL OR cr.start_date = :startDate) AND " +
            "(:endDate IS NULL OR cr.end_date = :endDate)", nativeQuery = true)
    List<CarReservation> findByCarAndStartDateEndDate(Long carId, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT r FROM CarReservation r WHERE r.car.id = :carId " +
            "AND r.endDate >= :startDate AND r.startDate <= :endDate")
    List<CarReservation> findOverlappingReservations(@Param("carId") Long carId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT r.car.id FROM CarReservation r WHERE " +
            "(:startDate BETWEEN r.startDate AND r.endDate OR " +
            ":endDate BETWEEN r.startDate AND r.endDate OR " +
            "(r.startDate BETWEEN :startDate AND :endDate) OR " +
            "(r.endDate BETWEEN :startDate AND :endDate))")
    List<Long> findReservedCarIdsByDateRange(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT * FROM car_reservations r WHERE " +
            ":userId IS NULL OR r.user_id = :userId ORDER BY r.start_date ASC", nativeQuery = true)
    List<CarReservation> findByUserIdOrderByStartDate(@Param("userId") Long userId);

}
