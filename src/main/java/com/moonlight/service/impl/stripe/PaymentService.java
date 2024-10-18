package com.moonlight.service.impl.stripe;

import com.moonlight.repository.bar.BarReservationRepository;
import com.moonlight.repository.car.CarReservationRepository;
import com.moonlight.repository.hotel.HotelRoomReservationRepository;
import com.moonlight.repository.restaurant.RestaurantReservationRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Data
public class PaymentService {
    @Autowired
    private HotelRoomReservationRepository hotelRoomReservationRepository;
    @Autowired
    private CarReservationRepository carReservationRepository;
    @Autowired
    private BarReservationRepository barReservationRepository;
    @Autowired
    private RestaurantReservationRepository restaurantReservationRepository;
    @Autowired
    private ReservationServiceImpl reservationServiceImpl;

    public Session createCheckoutSession(Long userId, String reservationType, Long reservationId, double amount, String currency, String successUrl, String cancelUrl)
            throws StripeException {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", String.valueOf(userId));
        metadata.put("reservationType", reservationType);
        metadata.put("reservationId", String.valueOf(reservationId));


        SessionCreateParams params = SessionCreateParams.builder()
                .setPaymentMethodOptions(SessionCreateParams.PaymentMethodOptions.builder()
                        .setCard(SessionCreateParams.PaymentMethodOptions.Card.builder().build()).build())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency(currency)
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Payment for user Id " + userId)
                                                        .build()
                                        )
                                        .setUnitAmount((long) amount * 100)
                                        .build()
                        )
                        .setQuantity(1L)
                        .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .putAllMetadata(metadata)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .build();
        Session session = Session.create(params);

        reservationServiceImpl.trackPayment(session.getPaymentIntent(), reservationId);

        return session;
    }
}