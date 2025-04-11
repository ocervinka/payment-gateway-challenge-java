package com.checkout.payment.gateway.bank.api;

/**
 * Record for request to the Acquiring Bank API
 * Example:
 * {
 *   "card_number": "2222405343248877",
 *   "expiry_date": "04/2025",
 *   "currency": "GBP",
 *   "amount": 100,
 *   "cvv": "123"
 * }
 */
public record BankRequest(
    String card_number,
    String expiry_date,
    String currency,
    Integer amount,
    String cvv
) {}