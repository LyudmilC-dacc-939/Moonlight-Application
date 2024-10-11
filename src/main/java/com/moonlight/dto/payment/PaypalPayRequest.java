package com.moonlight.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaypalPayRequest {
    @NotNull
    private String reservationType;
    @NotNull
    private Long reservationId;
}
