package com.moonlight.service.impl.bar;

import com.moonlight.model.bar.Seat;
import com.moonlight.repository.bar.SeatRepository;
import com.moonlight.service.BarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BarServiceImpl implements BarService {
    @Autowired
    private SeatRepository seatRepository;


    @Override
    public Set<Seat> searchByScreen(String screenName) {
        List<Seat> allSeats = seatRepository.findAll();
        Set<Seat> seats = new HashSet<>();
        for (Seat seat : allSeats) {
            if (seat.getScreen().getCurrentScreenName().toLowerCase().contains(screenName.toLowerCase())) {
                seats.add(seat);
            }
        }
        return seats;
    }

    @Override
    public Set<Seat> searchBySeatNumberAndByScreen(String screenName, Long seatNumber) {
        Set<Seat> allSeats = searchByScreen(screenName);
        Set<Seat> seats = new HashSet<>();
        for (Seat seat : allSeats) {
            if (seatNumber == seat.getSeatNumber()) {
                seats.add(seat);
            }
        }
        return seats;
    }
}
