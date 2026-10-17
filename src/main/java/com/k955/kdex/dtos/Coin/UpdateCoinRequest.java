package com.k955.kdex.dtos.Coin;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateCoinRequest(
        @Size(max = 50, message = "Name can have at most 50 characters")
        String name,

        @DecimalMin(value = "0.0001", message = "Volatility must be greater than zero")
        @DecimalMax(value = "20", message = "Volatility must be at most 20")
        @Digits(integer = 2, fraction = 4, message = "Volatility can have at most 4 decimal places")
        BigDecimal volatilityPct
) {
}
