package com.checkout.payment.gateway.bank;

public class BankException extends Exception {

  public BankException(String message, Throwable cause) {
    super(message, cause);
  }
}
