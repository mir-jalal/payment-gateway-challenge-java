package com.checkout.payment.gateway.strategy;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.mapper.PaymentMapper;

@Component
public class BankGateway implements GeneralPaymentGateway {

    private final String bankUrl = "http://localhost:8080/payments";
    private static final Logger LOG = LoggerFactory.getLogger(GeneralPaymentGateway.class);
    private final RestTemplate restTemplate;
    private final PaymentMapper paymentMapper;

    public BankGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.paymentMapper = new PaymentMapper();
    }

    @Override
    public Payment processPayment(PostPaymentRequest paymentRequest) {
        UUID id = UUID.randomUUID();
        Payment payment = new Payment();
        BankPaymentRequest bankPaymentRequest = paymentMapper.toBankPaymentRequest(paymentRequest);
        BankPaymentResponse bankPaymentResponse = sendPaymentRequest(bankPaymentRequest);

        payment.setId(id);
        payment.setCardNumberLastFour(bankPaymentRequest.getLastFourDigits());
        payment.setCurrency(paymentRequest.currency());
        payment.setAmount(paymentRequest.amount());
        payment.setExpiryMonth(paymentRequest.expiryMonth());
        payment.setExpiryYear(paymentRequest.expiryYear());
        if (bankPaymentResponse != null && bankPaymentResponse.isSuccess()) {
            payment.setStatus(PaymentStatus.AUTHORIZED);
        } else {
            payment.setStatus(PaymentStatus.DECLINED);
        }
        return payment;
    }
    
    private BankPaymentResponse sendPaymentRequest(BankPaymentRequest bankPaymentRequest) {
        try {
            ResponseEntity<BankPaymentResponse> response = this.restTemplate.postForEntity(bankUrl, bankPaymentRequest, BankPaymentResponse.class);
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            LOG.error("Error while calling bank gateway", ex);
            return new BankPaymentResponse(false, null);
        }
    }
}
