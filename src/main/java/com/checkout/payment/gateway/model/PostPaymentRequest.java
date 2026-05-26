package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

import org.hibernate.validator.constraints.CreditCardNumber;

public record PostPaymentRequest(

  @JsonProperty("card_number")
  @Size(min = 14, max = 19, message = "Card number must be between 14 and 19 digits")
  @NotBlank(message = "Card number is required")
  @CreditCardNumber(message = "Invalid card number")
  String cardNumber,
  @JsonProperty("expiry_month")
  @Min(value = 1, message = "Expiry month must be between 1 and 12")
  @Max(value = 12, message = "Expiry month must be between 1 and 12")
  int expiryMonth,
  @JsonProperty("expiry_year")
  @Min(value = 2000, message = "Expiry year must be in the future")
  // It only makes sure it's not some arbitrary year, there is a separate check in the service to make sure it's not expired
  int expiryYear,
  @Pattern(
        regexp = "^[A-Z]{3}$",
        message = "Currency must be 3 uppercase letters"
    )
  String currency,
  @Min(value = 1, message = "Amount must be greater than 0")
  int amount,
  @Pattern(
        regexp = "^[0-9]{3,4}$",
        message = "CVV must be 3 or 4 digits"
    )
  String cvv
  ) implements Serializable {

  public String getCardLastFourDigits() {
    if (this.cardNumber != null && this.cardNumber.length() >= 4) {
      return this.cardNumber.substring(this.cardNumber.length() - 4);
    }
    return null;
  }
  
  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%d/%d", expiryMonth, expiryYear);
  }
}
