package com.moonlight.controller.payment;

import com.moonlight.advice.exception.InvalidInputException;
import com.moonlight.advice.exception.PayPalServiceException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.dto.payment.PaypalPayRequest;
import com.moonlight.model.user.User;
import com.moonlight.service.PayService;
import com.moonlight.service.payment.PayPalService;
import com.paypal.api.payments.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/payments/paypal")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "API for PayPal Payments")
public class PaymentPaypalController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private PayService payService;

    @Operation(
            summary = "Complete PayPal payment for selected reservationId",
            description = "Completes the PayPal payment process after the user has approved the payment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid paymentId or payerId provided"),
            @ApiResponse(responseCode = "500", description = "Payment execution failed or other error occurred")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_ADMIN')")
    @PostMapping("/pay-reservation")
    public ResponseEntity<String> paySelectedReservation(@Valid @RequestBody PaypalPayRequest request, @AuthenticationPrincipal User user) {


        try {
            // Passing the information to the Service
            String redirectUrl = payService.handleReservationPayment(
                    user.getId(),
                    request.getReservationType(),
                    request.getReservationId()
            );

            // If the previous checks all go through, redirect to PayPal approval
            return ResponseEntity.ok("Redirect to Paypal: " + redirectUrl);
        } catch (InvalidInputException | PayPalServiceException | RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/success")
    public String completePayment(@RequestParam("paymentId") String paymentId,
                                  @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                String reservationId = extractReservationId(payment);
                String reservationType = extractReservationType(payment);

                // Update reservation status
                payService.confirmReservation(reservationId, reservationType);

                return "successPage";
            }
        } catch (InvalidInputException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    public String extractReservationId(Payment payment) {
        String description = payment.getTransactions().get(0).getDescription();

        String idIndicator = "for ID: ";

        int idStartIndex = description.indexOf(idIndicator);

        if (idStartIndex != -1) {
            // Calculate the start index of the actual reservation ID
            int reservationIdStart = idStartIndex + idIndicator.length();

            // Extract the reservation ID
            return description.substring(reservationIdStart).trim();
        }
        throw new RecordNotFoundException("Reservation ID not found in payment description.");
    }

    public String extractReservationType(Payment payment) {
        String description = payment.getTransactions().get(0).getDescription();

        // The part of the description that marks where the reservation type ends
        String separator = " Reservation payment for ID: ";

        // Find the index of the separator in the description
        int separatorIndex = description.indexOf(separator);

        if (separatorIndex != -1) {
            // Extract the reservation type (everything before the separator)
            String reservationType = description.substring(0, separatorIndex).trim();

            // Return the reservation type with no spaces
            return reservationType.replaceAll("\\s+", "");
        }

        throw new RecordNotFoundException("Reservation type not found in payment description.");
    }
}
