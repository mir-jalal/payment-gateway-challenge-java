package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.model.mapper.PaymentMapper;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.impl.PaymentGatewayServiceImpl;
import com.checkout.payment.gateway.strategy.GeneralPaymentGateway;
import java.time.Year;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PaymentGatewayServiceImplTest {

  @Test
  void processPaymentAuthorizedAddsToRepositoryAndReturnsResponse() {
    PaymentsRepository paymentsRepository = new PaymentsRepository();
    GeneralPaymentGateway gateway = new GeneralPaymentGateway() {
      @Override
      public Payment processPayment(PostPaymentRequest paymentRequest) {
        Payment p = new Payment();
        p.setId(UUID.randomUUID());
        p.setStatus(PaymentStatus.AUTHORIZED);
        p.setCardNumberLastFour(paymentRequest.getCardLastFourDigits());
        p.setCurrency(paymentRequest.currency());
        p.setAmount(paymentRequest.amount());
        p.setExpiryMonth(paymentRequest.expiryMonth());
        p.setExpiryYear(paymentRequest.expiryYear());
        return p;
      }
    };
    PaymentMapper mapper = new PaymentMapper();

    PaymentGatewayServiceImpl service = new PaymentGatewayServiceImpl(paymentsRepository, gateway, mapper);

    PostPaymentRequest request = new PostPaymentRequest(
        "4242424242424242",
        12,
        Year.now().getValue() + 1,
        "USD",
        100,
        "123"
    );

    PostPaymentResponse response = service.processPayment(request);

    assertEquals(PaymentStatus.AUTHORIZED, response.status());
    assertEquals(100, response.amount());
    assertEquals("USD", response.currency());

    // ensure it was stored
    assertEquals(true, paymentsRepository.get(response.id()).isPresent());
  }

  @Test
  void processPaymentWithExpiredCardThrows() {
    PaymentsRepository paymentsRepository = new PaymentsRepository();
    GeneralPaymentGateway gateway = new GeneralPaymentGateway() {
      @Override
      public Payment processPayment(PostPaymentRequest paymentRequest) {
        throw new IllegalStateException("should not be called");
      }
    };
    PaymentMapper mapper = new PaymentMapper();

    PaymentGatewayServiceImpl service = new PaymentGatewayServiceImpl(paymentsRepository, gateway, mapper);

    PostPaymentRequest request = new PostPaymentRequest(
        "4242424242424242",
        1,
        Year.now().getValue() - 1,
        "USD",
        100,
        "123"
    );

    assertThrows(EventProcessingException.class, () -> service.processPayment(request));
  }

  @Test
  void getPaymentByIdWithRandomIdThrows() {
    PaymentsRepository paymentsRepository = new PaymentsRepository();
    GeneralPaymentGateway gateway = new GeneralPaymentGateway() {
      @Override
      public Payment processPayment(PostPaymentRequest paymentRequest) {
        throw new IllegalStateException("not used");
      }
    };
    PaymentMapper mapper = new PaymentMapper();

    PaymentGatewayServiceImpl service = new PaymentGatewayServiceImpl(paymentsRepository, gateway, mapper);

    UUID id = UUID.randomUUID();

    assertThrows(EventProcessingException.class, () -> service.getPaymentById(id));
  }
}
