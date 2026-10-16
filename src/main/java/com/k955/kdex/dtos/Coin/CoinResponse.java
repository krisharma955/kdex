package com.k955.kdex.dtos.Coin;

import java.math.BigDecimal;
import java.time.Instant;

public record CoinResponse(
        String symbol,
        String name,
        BigDecimal currentPrice,
        BigDecimal previousPrice,
        BigDecimal volatilityPct,
        boolean isActive,
        Instant updatedAt
) {
}
