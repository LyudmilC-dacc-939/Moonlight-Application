package com.moonlight.service;

import com.moonlight.dto.bar.BarReservationRequest;
import com.moonlight.dto.bar.BarReservationResponse;
import com.moonlight.model.user.User;

public interface BarReservationService {
    BarReservationResponse createReservation(BarReservationRequest request, User user);

}
