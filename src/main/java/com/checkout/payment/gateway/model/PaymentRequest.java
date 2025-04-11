package com.checkout.payment.gateway.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.YearMonth;

public record PaymentRequest(
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{14,19}$", message = "Card number must be numeric and between 14-19 digits")
    String cardNumber,

    @NotNull(message = "Expiry month is required")
    @Min(value = 1, message = "Expiry month must be between 1 and 12")
    @Max(value = 12, message = "Expiry month must be between 1 and 12")
    Integer expiryMonth,

    @NotNull(message = "Expiry year is required")
    // Consider adding a custom validator annotation for future year if needed,
    // or perform check in service layer after basic validation.
    Integer expiryYear,

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    // Add validation for specific allowed currencies if required
    String currency,

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be positive")
    Integer amount, // Represents minor currency unit

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be numeric and 3-4 digits long")
    String cvv
) {


  public boolean isExpiryDateValid() {
    if (expiryYear == null || expiryMonth == null) {
      return false;
    }
    try {
      YearMonth expiry = YearMonth.of(expiryYear, expiryMonth);
      return expiry.isAfter(YearMonth.now());
    } catch (Exception e) {
      return false;
    }
  }
}
