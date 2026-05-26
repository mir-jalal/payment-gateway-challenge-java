package com.checkout.payment.gateway.service.impl;

import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.model.mapper.PaymentMapper;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.checkout.payment.gateway.strategy.GeneralPaymentGateway;

import java.time.YearMonth;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final GeneralPaymentGateway generalGateway;
  private final PaymentMapper paymentMapper;

  public PaymentGatewayServiceImpl(PaymentsRepository paymentsRepository, GeneralPaymentGateway generalGateway, PaymentMapper paymentMapper) {
    this.paymentsRepository = paymentsRepository;
    this.generalGateway = generalGateway;
    this.paymentMapper = paymentMapper;
  }

  @Override
  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    Payment payment = paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
    return paymentMapper.toPostPaymentResponse(payment);
  }

  @Override
  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    LOG.debug("Processing payment request for card number ending with {}", paymentRequest.getCardLastFourDigits());
    validatePaymentRequest(paymentRequest);

    Payment payment = generalGateway.processPayment(paymentRequest);
    paymentsRepository.add(payment);
    return paymentMapper.toPostPaymentResponse(payment);
  }

  private Boolean validateExpiryDate(int month, int year) {
      YearMonth expiryDate = YearMonth.of(year, month);
      if (expiryDate.isBefore(YearMonth.now())) {
          return false;
      }
      return true;
  }

  private void validatePaymentRequest(PostPaymentRequest paymentRequest) {
    if (!validateExpiryDate(paymentRequest.expiryMonth(), paymentRequest.expiryYear())) {
      Payment payment = paymentMapper.toRejectedPayment(paymentRequest);
      paymentsRepository.add(payment);
      throw new EventProcessingException("Card has expired");
    }
  }
}
