package com.moonlight.service;

import com.moonlight.model.bar.Seat;

import java.util.Set;


public interface BarService {
    Set<Seat> searchByScreen(String screenName);

    Set<Seat> searchBySeatNumberAndByScreen(String screenName, Long seatNumber);
}
