package com.moonlight.repository.bar;

import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.bar.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BarReservationRepository extends JpaRepository<BarReservation, Long> {
    @Query("SELECT COUNT(br) > 0 FROM BarReservation br JOIN br.seats seat " +
            "WHERE seat = :seat AND br.reservationDate = :reservationDate")
    boolean existsBySeatAndReservationDate(@Param("seat") Seat seat, @Param("reservationDate") LocalDate reservationDate);
}
