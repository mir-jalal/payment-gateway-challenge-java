package com.checkout.payment.gateway.model.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;

@Component
public class PaymentMapper {
    public PostPaymentResponse toPostPaymentResponse(Payment payment) {
        PostPaymentResponse response = new PostPaymentResponse(
            payment.getId(),
            payment.getStatus(),
            payment.getCardNumberLastFour(),
            payment.getExpiryMonth(),
            payment.getExpiryYear(),
            payment.getCurrency(),
            payment.getAmount()
        );
        return response;
    }

    public BankPaymentRequest toBankPaymentRequest(PostPaymentRequest paymentRequest) {
        BankPaymentRequest bankPaymentRequest = new BankPaymentRequest(
            paymentRequest.cardNumber(),
            paymentRequest.getExpiryDate(),
            paymentRequest.currency(),
            paymentRequest.amount(),
            paymentRequest.cvv()
        );
        return bankPaymentRequest;
    }

    public Payment toRejectedPayment(PostPaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setCardNumberLastFour(paymentRequest.getCardLastFourDigits());
        payment.setCurrency(paymentRequest.currency());
        payment.setAmount(paymentRequest.amount());
        payment.setExpiryMonth(paymentRequest.expiryMonth());
        payment.setExpiryYear(paymentRequest.expiryYear());
        payment.setStatus(PaymentStatus.REJECTED);
        return payment;
    }
}
