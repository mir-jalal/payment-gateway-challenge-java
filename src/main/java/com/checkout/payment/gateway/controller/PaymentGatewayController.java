package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
    this.paymentGatewayService = paymentGatewayService;
  }

  @GetMapping("/payment/{id}")
  public ResponseEntity<PostPaymentResponse> getPostPaymentEventById(@PathVariable UUID id) {
    PostPaymentResponse response = paymentGatewayService.getPaymentById(id);
    if (response == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok().body(response);
  }

  @PostMapping("/payment")
  public ResponseEntity<PostPaymentResponse> processPayment(@RequestBody @Valid PostPaymentRequest request) {

    PostPaymentResponse response = paymentGatewayService.processPayment(request);

    if (response.status() == PaymentStatus.AUTHORIZED) {
      URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
          .buildAndExpand(response.id()).toUri();
      return ResponseEntity.created(location).body(response);
    }

    return ResponseEntity.badRequest().body(response);
  }
}
