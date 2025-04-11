package com.checkout.payment.gateway.model;

import java.util.UUID;

public record PaymentResponse(
    UUID id,
    PaymentStatus status,
    int cardNumberLastFour,
    Integer expiryMonth,
    Integer expiryYear,
    String currency,
    int amount
) {
}
