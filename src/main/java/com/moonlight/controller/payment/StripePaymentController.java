package com.moonlight.controller.payment;

import com.moonlight.model.user.User;
import com.moonlight.service.impl.stripe.PaymentService;
import com.moonlight.service.impl.stripe.ReservationServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/payments/stripe")
public class StripePaymentController {
    @Autowired
    private final PaymentService paymentService;
    @Autowired
    private final ReservationServiceImpl reservationServiceImpl;

    @Autowired
    public StripePaymentController(PaymentService paymentService, ReservationServiceImpl reservationServiceImpl) {
        this.paymentService = paymentService;
        this.reservationServiceImpl = reservationServiceImpl;
    }

    @Operation(
            summary = "Complete Stripe payment for selected reservationId",
            description = "Completes the Stripe payment process after the user has approved the payment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid paymentId or payerId provided"),
            @ApiResponse(responseCode = "500", description = "Payment execution failed or other error occurred")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_ADMIN')")
    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(
            @AuthenticationPrincipal User user,
            @RequestParam String reservationType,
            @RequestParam Long reservationId,
            @RequestParam String currency) {
        try {
            double totalAmount = reservationServiceImpl.calculateTotalPendingAmount(user.getId(), reservationType, reservationId);
            if (totalAmount == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No pending reservations found for this user");

            }
            String successUrl = "http://localhost:8090/api/v1/payments/stripe/checkout/success";
            String cancelUrl = "http://localhost:8090/api/v1/payments/stripe/checkout/cancel";


            Session session = paymentService.createCheckoutSession(user.getId(), reservationType, reservationId,
                    totalAmount, currency, successUrl, cancelUrl);

            return ResponseEntity.ok(Map.of("checkoutUrl", session.getUrl()));
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/checkout/success")
    public String successPage() {

        return "<h1> You did it!<h1><p>Your payment was successful. Thank you for your purchase</p>";
    }

    @GetMapping("/checkout/cancel")
    public String cancelPage() {
        return "<h1> Oops! You lost yourself<h1><p>It seems you cancel the payment. Feel free to try again later</p>";
    }
}