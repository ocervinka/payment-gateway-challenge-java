package com.checkout.payment.gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runs Bank Simulator and configures application to connect to it.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class IntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Container
  public static GenericContainer<?> bankSimulator = new GenericContainer<>("bbyars/mountebank:2.8.1")
      .withExposedPorts(2525, 8080)
      .withCommand("--configfile /imposters/bank_simulator.ejs --allowInjection --debug")
      .withFileSystemBind("./imposters", "/imposters")
      .waitingFor(
          Wait.forHttp("/")
              .forPort(2525)
              .forStatusCode(200)
              .withStartupTimeout(Duration.ofSeconds(30))
      );

  @DynamicPropertySource
  static void registerBankSimulatorProperties(DynamicPropertyRegistry registry) {
    String simulatorUrl = String.format("http://%s:%d",
        bankSimulator.getHost(),
        bankSimulator.getMappedPort(8080)
    );
    registry.add("bank.api.url", () -> simulatorUrl);
  }

  @Test
  void whenBankAuthorizedPaymentAuthorizedStatusIsReturned() throws Exception {
    mvc.perform(post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "cardNumber": "4111111111111111",
                  "expiryMonth": 12,
                  "expiryYear": 2026,
                  "currency": "USD",
                  "amount": 100,
                  "cvv":
                  "123"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("Authorized"))
        .andExpect(jsonPath("$.cardNumberLastFour").value("1111"))
        .andExpect(jsonPath("$.expiryMonth").value("12"))
        .andExpect(jsonPath("$.expiryYear").value("2026"))
        .andExpect(jsonPath("$.currency").value("USD"))
        .andExpect(jsonPath("$.amount").value("100"));
  }

  @Test
  void whenBankDeclinedPaymentDeclinedStatusIsReturned() throws Exception {
    mvc.perform(post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "cardNumber": "4111111111111112",
                  "expiryMonth": 12,
                  "expiryYear": 2026,
                  "currency": "USD",
                  "amount": 100,
                  "cvv":
                  "123"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("Declined"))
        .andExpect(jsonPath("$.cardNumberLastFour").value("1112"))
        .andExpect(jsonPath("$.expiryMonth").value("12"))
        .andExpect(jsonPath("$.expiryYear").value("2026"))
        .andExpect(jsonPath("$.currency").value("USD"))
        .andExpect(jsonPath("$.amount").value("100"));
  }

  @Test
  void whenBankReturned503() throws Exception {
    mvc.perform(post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "cardNumber": "4111111111111110",
                  "expiryMonth": 12,
                  "expiryYear": 2026,
                  "currency": "USD",
                  "amount": 100,
                  "cvv":
                  "123"
                }
                """))
        .andExpect(status().isServiceUnavailable());
  }

  @Test
  void whenCardNumberIsMissing() throws Exception {
    mvc.perform(post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "expiryMonth": 12,
                  "expiryYear": 2026,
                  "currency": "USD",
                  "amount": 100,
                  "cvv":
                  "123"
                }
                """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenCardNumberHasNotEnoughDigits() throws Exception {
    mvc.perform(post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "cardNumber": "4111111111111",
                  "expiryMonth": 12,
                  "expiryYear": 2026,
                  "currency": "USD",
                  "amount": 100,
                  "cvv":
                  "123"
                }
                """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenCardNumberHasNotTooManyDigits() throws Exception {
    mvc.perform(post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "cardNumber": "41111111111111111111",
                  "expiryMonth": 12,
                  "expiryYear": 2026,
                  "currency": "USD",
                  "amount": 100,
                  "cvv":
                  "123"
                }
                """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenCardNumberContainsOtherCharactersThanNumbers() throws Exception {
    mvc.perform(post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "cardNumber": "4111-1111-1111-1111",
                  "expiryMonth": 12,
                  "expiryYear": 2026,
                  "currency": "USD",
                  "amount": 100,
                  "cvv":
                  "123"
                }
                """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenCardExpirationIsInPast() throws Exception {
    mvc.perform(post("/api/v1/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "cardNumber": "4111111111111111",
                  "expiryMonth": 3,
                  "expiryYear": 2025,
                  "currency": "USD",
                  "amount": 100,
                  "cvv":
                  "123"
                }
                """))
        .andExpect(status().isBadRequest());
  }
}