package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.bank.BankClient;
import com.checkout.payment.gateway.bank.BankException;
import com.checkout.payment.gateway.bank.api.BankResponse;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final BankClient bankClient;
  private final PaymentsRepository paymentsRepository;


  public PaymentGatewayService(BankClient bankClient, PaymentsRepository paymentsRepository) {
    this.bankClient = bankClient;
    this.paymentsRepository = paymentsRepository;
  }

  public PaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    return paymentsRepository.findById(id).orElseThrow(
        () -> new EventProcessingException("Payment not found", HttpStatus.NOT_FOUND));
  }

  public PaymentResponse processPayment(PaymentRequest paymentRequest) {
    try {
      UUID paymentId = UUID.randomUUID();
      LOG.debug("Processing payment with ID {}", paymentId);
      BankResponse bankResponse = bankClient.processPayment(paymentRequest);
      PaymentResponse paymentResponse = new PaymentResponse(
          paymentId,
          bankResponse.authorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED,
          getLastFourDigits(paymentRequest.cardNumber()),
          paymentRequest.expiryMonth(),
          paymentRequest.expiryYear(),
          paymentRequest.currency(),
          paymentRequest.amount()
      );
      paymentsRepository.add(paymentResponse);
      return paymentResponse;
    } catch (BankException e) {
      throw new EventProcessingException("Bank Error", HttpStatus.SERVICE_UNAVAILABLE);
    }
  }

  private int getLastFourDigits(String cardNumber) {
    return Integer.parseInt(cardNumber.substring(cardNumber.length() - 4));
  }
}
