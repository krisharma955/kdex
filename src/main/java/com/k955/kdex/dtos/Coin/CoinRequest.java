package com.k955.kdex.dtos.Coin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CoinRequest(
        @NotBlank String symbol,
        @NotBlank String name,
        @NotNull BigDecimal currentPrice,
        @NotNull BigDecimal previousPrice,
        @NotNull BigDecimal volatilityPct
) {
}
