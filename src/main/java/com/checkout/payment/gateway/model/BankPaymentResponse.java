package com.checkout.payment.gateway.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BankPaymentResponse(
    Boolean authorized,
    @JsonProperty("authorization_code")
    String authorizationCode
) implements Serializable {
    public Boolean isSuccess() {
        return authorized;
    }
}
