package com.moonlight.service;

import com.moonlight.dto.car.CarReservationRequest;
import com.moonlight.model.car.CarReservation;

public interface CarReservationService {
    CarReservation createReservation(CarReservationRequest request, String email);
}
