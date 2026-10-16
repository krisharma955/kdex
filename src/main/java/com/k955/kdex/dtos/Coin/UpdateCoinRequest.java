package com.k955.kdex.dtos.Coin;

import java.math.BigDecimal;

public record UpdateCoinRequest(
        String name,
        BigDecimal volatilityPct
) {
}
