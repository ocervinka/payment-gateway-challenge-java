package com.checkout.payment.gateway.bank;

import com.checkout.payment.gateway.bank.api.BankRequest;
import com.checkout.payment.gateway.bank.api.BankResponse;
import com.checkout.payment.gateway.model.PaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class BankClientImpl implements BankClient {

    private final RestClient restClient;


    public BankClientImpl(RestClient.Builder restClientBuilder, @Value("${bank.api.url}") String bankApiUrl) {
       this.restClient = restClientBuilder
                            .baseUrl(bankApiUrl)
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .build();
    }

    @Override
    public BankResponse processPayment(PaymentRequest paymentRequest) throws BankException {
        String path = "/payments";

        String expiryDate = String.format(
            "%02d/%d", paymentRequest.expiryMonth(), paymentRequest.expiryYear());

        BankRequest bankRequest = new BankRequest(
            paymentRequest.cardNumber(),
            expiryDate,
            paymentRequest.currency(),
            paymentRequest.amount(),
            paymentRequest.cvv()
        );

        try {
            return restClient
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bankRequest)
                .retrieve()
                .toEntity(BankResponse.class) // convert response body, exceptions thrown for non-2xx
                .getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) { // 4xx and 5xx
            throw new BankException("Bank API Error: " + e.getStatusCode(), e);
        } catch (RestClientException e) { // connection errors, timeouts, response parsing errors etc.
            throw new BankException("Communication Error with Bank API", e);
        }
    }
}