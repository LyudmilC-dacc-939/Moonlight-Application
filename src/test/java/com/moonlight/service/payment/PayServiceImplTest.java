package com.moonlight.service.payment;

import com.moonlight.advice.exception.InvalidInputException;
import com.moonlight.advice.exception.PayPalServiceException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.repository.bar.BarReservationRepository;
import com.moonlight.repository.car.CarReservationRepository;
import com.moonlight.repository.hotel.HotelRoomReservationRepository;
import com.moonlight.repository.restaurant.RestaurantReservationRepository;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PayServiceImplTest {

    @InjectMocks
    private PayServiceImpl payService;

    @Mock
    private BarReservationRepository barReservationRepository;
    @Mock
    private CarReservationRepository carReservationRepository;
    @Mock
    private HotelRoomReservationRepository hotelRoomReservationRepository;
    @Mock
    private RestaurantReservationRepository restaurantReservationRepository;
    @Mock
    private PayPalService payPalService;

    @Test
    public void testHandleReservationPayment_HappyPath() throws Exception {
        Long userId = 1L;
        Long reservationId = 123L;
        BarReservation barReservation = new BarReservation();
        barReservation.setId(reservationId);
        barReservation.setTotalCost(100.0);

        when(barReservationRepository.findByUserId(userId)).thenReturn(Collections.singletonList(barReservation));
        Payment payment = mock(Payment.class);
        Links link = new Links();
        link.setRel("approval_url");
        link.setHref("http://paypal.com/approve");
        when(payment.getLinks()).thenReturn(Collections.singletonList(link));
        when(payPalService.createPayment(anyDouble(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(payment);

        String redirectUrl = payService.handleReservationPayment(userId, "Bar", reservationId);
        assertEquals("http://paypal.com/approve", redirectUrl);
    }

    @Test
    public void testHandleReservationPayment_InvalidType() {
        assertThrows(InvalidInputException.class, () -> {
            payService.handleReservationPayment(1L, "InvalidType", 123L);
        });
    }

    @Test
    public void testHandleReservationPayment_NoReservationsFound() {
        when(barReservationRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        assertThrows(RecordNotFoundException.class, () -> {
            payService.handleReservationPayment(1L, "Bar", 123L);
        });
    }

    @Test
    public void testHandleReservationPayment_ReservationIdNotFound() {
        BarReservation barReservation = new BarReservation();
        barReservation.setId(456L);
        when(barReservationRepository.findByUserId(1L)).thenReturn(Collections.singletonList(barReservation));
        assertThrows(RecordNotFoundException.class, () -> {
            payService.handleReservationPayment(1L, "Bar", 123L);
        });
    }

    @Test
    public void testHandleReservationPayment_PayPalServiceError() throws PayPalRESTException {
        BarReservation barReservation = new BarReservation();
        barReservation.setId(123L);
        when(barReservationRepository.findByUserId(1L)).thenReturn(Collections.singletonList(barReservation));
        when(payPalService.createPayment(anyDouble(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(PayPalRESTException.class);
        assertThrows(PayPalServiceException.class, () -> {
            payService.handleReservationPayment(1L, "Bar", 123L);
        });
    }

}