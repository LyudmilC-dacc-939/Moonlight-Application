package com.moonlight.service;

import com.moonlight.dto.bar.BarReservationRequest;
import com.moonlight.dto.bar.BarReservationResponse;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.user.User;

import java.util.List;

public interface BarReservationService {
    BarReservationResponse createReservation(BarReservationRequest request, User user);
    List<BarReservation> getBarReservationsByUserId(Long userId);
}
