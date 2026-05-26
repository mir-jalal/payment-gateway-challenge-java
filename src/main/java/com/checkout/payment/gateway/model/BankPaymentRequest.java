package com.checkout.payment.gateway.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BankPaymentRequest(
    @JsonProperty("card_number")
    String cardNumber,
    @JsonProperty("expiry_date")
    String expiryDate,
    String currency,
    Integer amount,
    String cvv
) implements Serializable {
    public String getLastFourDigits() {
        if (cardNumber != null && cardNumber.length() >= 4) {
            return cardNumber.substring(cardNumber.length() - 4);
        }
        return null;
    }
}
