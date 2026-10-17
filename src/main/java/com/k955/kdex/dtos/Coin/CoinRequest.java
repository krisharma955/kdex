package com.k955.kdex.dtos.Coin;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CoinRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9]{2,10}$", message = "Invalid symbol")
        String symbol,

        @NotBlank
        @Size(max = 50, message = "Name can have at most 50 characters")
        String name,

        @NotNull
        @DecimalMin(value = "0.00000001", message = "Current price must be greater than zero")
        @Digits(integer = 11, fraction = 8, message = "Current price can have at most 8 decimal places")
        BigDecimal currentPrice,

        @NotNull
        @DecimalMin(value = "0.00000001", message = "Previous price must be greater than zero")
        @Digits(integer = 11, fraction = 8, message = "Previous price can have at most 8 decimal places")
        BigDecimal previousPrice,

        @NotNull
        @DecimalMin(value = "0.0001", message = "Volatility must be greater than zero")
        @DecimalMax(value = "20", message = "Volatility must be at most 20")
        @Digits(integer = 2, fraction = 4, message = "Volatility can have at most 4 decimal places")
        BigDecimal volatilityPct
) {
}
