package com.k955.kdex.dtos.Coin;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketPriceUpdate(
        String symbol,
        BigDecimal previousPrice,
        BigDecimal currentPrice,
        BigDecimal changePct,
        Instant updatedAt
) {
}
