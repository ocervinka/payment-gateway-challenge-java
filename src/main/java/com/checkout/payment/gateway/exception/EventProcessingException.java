package com.checkout.payment.gateway.exception;

import org.springframework.http.HttpStatusCode;

public class EventProcessingException extends RuntimeException {

  private final HttpStatusCode statusCode;

  public EventProcessingException(String message, HttpStatusCode statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public HttpStatusCode getStatusCode() {
    return statusCode;
  }
}
