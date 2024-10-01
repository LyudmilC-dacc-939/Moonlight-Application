package com.moonlight.repository.bar;

import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BarReservationRepository extends JpaRepository<BarReservation, Long> {

    @Query("SELECT COUNT(br) > 0 FROM BarReservation br JOIN br.seats seat " +
            "WHERE seat = :seat AND br.reservationDate = :reservationDate")
    boolean existsBySeatAndReservationDate(@Param("seat") Seat seat, @Param("reservationDate") LocalDate reservationDate);

    @Query(value = "SELECT * FROM bar_reservations b " +
            "WHERE :userId IS NULL OR b.user_id = :userId " +
            "ORDER BY b.reservation_date ASC", nativeQuery = true)
    List<BarReservation> findByUserId(Long userId);

    List<BarReservation> findByScreenAndReservationDate(Screen screen, LocalDate reservationDate);
}