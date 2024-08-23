package com.moonlight.service;

import com.moonlight.dto.car.CarAvailabilityRequest;
import com.moonlight.dto.car.CarReservationRequest;
import com.moonlight.model.car.CarReservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CarReservationService {
    CarReservation createReservation(CarReservationRequest request, String email);
    Map<LocalDate, List<String>> getAvailableCarsByDateRange(CarAvailabilityRequest request);
}
