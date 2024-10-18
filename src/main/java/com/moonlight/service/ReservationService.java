package com.moonlight.service;

public interface ReservationService {

   double calculateTotalPendingAmount(Long userId, String reservationType, Long reservationId);

    void updateReservationStatus(String reservationId, String reservationType);
}
