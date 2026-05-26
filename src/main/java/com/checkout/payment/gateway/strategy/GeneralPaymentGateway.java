package com.checkout.payment.gateway.strategy;

import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PostPaymentRequest;

public interface GeneralPaymentGateway {
    public Payment processPayment(PostPaymentRequest paymentRequest);
}
