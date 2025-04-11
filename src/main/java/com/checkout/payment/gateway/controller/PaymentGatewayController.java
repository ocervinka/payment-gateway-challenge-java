package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
    this.paymentGatewayService = paymentGatewayService;
  }

  @PostMapping("/payment")
  public ResponseEntity<PaymentResponse> processPayment(
      @Valid @RequestBody PaymentRequest paymentRequest, BindingResult bindingResult)
      throws MethodArgumentNotValidException {

    if (!paymentRequest.isExpiryDateValid()) {
      var fieldError = new FieldError(
          "paymentRequest",
          "expiryDate",
          "Card expiration date must be in the future");
      bindingResult.addError(fieldError);
    }

    if (bindingResult.hasErrors()) {
      throw new MethodArgumentNotValidException(null, bindingResult);
    }

    PaymentResponse response = paymentGatewayService.processPayment(paymentRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/payment/{id}")
  public ResponseEntity<PaymentResponse> gePaymentById(@PathVariable UUID id) {
    return new ResponseEntity<>(paymentGatewayService.getPaymentById(id), HttpStatus.OK);
  }
}
