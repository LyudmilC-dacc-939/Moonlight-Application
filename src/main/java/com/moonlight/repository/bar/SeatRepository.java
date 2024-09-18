package com.moonlight.repository.bar;

import com.moonlight.model.bar.Screen;
import com.moonlight.model.bar.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findBySeatNumber(int seatNumber);

    Optional<Seat> findBySeatNumberAndScreen(int seatNumber, Screen screen);

}
