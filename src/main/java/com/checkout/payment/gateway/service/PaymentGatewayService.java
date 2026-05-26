package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import java.util.UUID;

public interface PaymentGatewayService {

  public PostPaymentResponse getPaymentById(UUID id) ;
  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest);
}
