package com.moonlight.controller.payment;

import com.moonlight.service.impl.stripe.ReservationServiceImpl;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/webhooks")
public class StripeWebhookController {
    @Autowired
    private final ReservationServiceImpl reservationService;

    @Autowired
    public StripeWebhookController(ReservationServiceImpl reservationService) {
        this.reservationService = reservationService;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = "sk_test_51Q8S6909YXyOjhuaYH6ryNBr3UVFGvOrGHiYhozPedFPAkiuDrUMdfEi7f7xOmgdxt6HHNdPXLLPSDnL7QnMMfzO00IQbJCZAi";
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-signature") String sigHeader) {
        String endpointSecret = "whsec_9004f866b04260d293e6a0adf1e936d4d47a1ae78d89482ef2bdc3eb629fad69";

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Stripe signature");
        }
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().get();
            if (session != null) {
                Map<String, String> metadata = session.getMetadata();
                String reservationId = metadata.get("reservationId");
                String reservationType = metadata.get("reservationType");

                reservationService.updateReservationStatus(reservationId, reservationType);
                return ResponseEntity.ok("Reservation status updated for ID: " + reservationId);
            }
        }
        return ResponseEntity.ok("Webhook processed successfully");
    }
}