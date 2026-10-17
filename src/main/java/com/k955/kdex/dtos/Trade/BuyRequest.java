package com.k955.kdex.dtos.Trade;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record BuyRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9]{2,10}$", message = "Invalid symbol")
        String symbol,

        @NotNull
        @DecimalMin(value = "0.00000001", message = "Quantity must be greater than zero")
        @Digits(integer = 11, fraction = 8, message = "Quantity can have at most 8 decimal places")
        BigDecimal quantity
) {
}
