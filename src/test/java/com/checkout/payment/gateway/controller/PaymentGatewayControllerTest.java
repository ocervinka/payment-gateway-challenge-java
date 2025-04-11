package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.model.PaymentResponse;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PaymentResponse payment = new PaymentResponse(
        UUID.randomUUID(),
        PaymentStatus.AUTHORIZED,
        4321,
        12,
        2024,
        "USD",
        4321);

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/api/v1/payment/" + payment.id()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.status().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.cardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.expiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.expiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.currency()))
        .andExpect(jsonPath("$.amount").value(payment.amount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/api/v1/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Payment not found"));
  }
}
