package com.checkout.payment.gateway.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.checkout.payment.gateway.model.PaymentResponse;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepository {

  private final Map<UUID, PaymentResponse> payments = new ConcurrentHashMap<>();


  public void add(PaymentResponse payment) {
    payments.put(payment.id(), payment);
  }

  public Optional<PaymentResponse> findById(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

}
