package com.k955.kdex.dtos.Trade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BuyRequest(
        @NotBlank String symbol,
        @NotNull BigDecimal quantity
) {
}
