package com.k955.kdex.dtos.Coin;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CoinHistory(
    String symbol,
    String name,
    BigDecimal currentPrice,
    BigDecimal previousPrice,
    List<PricePoint> history
) {

    public record PricePoint(
            BigDecimal price,
            Instant recordedAt
    ) {}

}
