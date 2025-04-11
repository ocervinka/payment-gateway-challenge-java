package com.checkout.payment.gateway.bank;

import com.checkout.payment.gateway.bank.api.BankResponse;
import com.checkout.payment.gateway.model.PaymentRequest;

public interface BankClient {
  BankResponse processPayment(PaymentRequest paymentRequest) throws BankException;
}
