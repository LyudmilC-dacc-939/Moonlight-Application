package com.moonlight.service;

import com.moonlight.advice.exception.InvalidInputException;
import com.moonlight.advice.exception.PayPalServiceException;
import com.moonlight.advice.exception.RecordNotFoundException;

public interface PayService {
    String handleReservationPayment(Long userId, String reservationType, Long reservationId) throws InvalidInputException, RecordNotFoundException, PayPalServiceException;
    void confirmReservation(String reservationId, String reservationType) throws InvalidInputException ;
}
