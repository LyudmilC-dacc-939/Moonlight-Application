package com.moonlight.repository.bar;

import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    boolean existsByScreenAndSeatNumber(Screen screen, int seatNumber);

    Optional<Seat> findByScreenAndSeatNumber(Screen screen, int seatNumber);

    List<Seat> findByScreen(Screen screen);
}