package com.checkout.payment.gateway.bank.api;

/**
 * Record for response from the Acquiring Bank API
 * Example:
 * {
 *   "authorized": true,
 *   "authorization_code": "0bb07405-6d44-4b50-a14f-7ae0beff13ad"
 * }
 */
public record BankResponse(
    Boolean authorized,
    String authorization_code
) {
}