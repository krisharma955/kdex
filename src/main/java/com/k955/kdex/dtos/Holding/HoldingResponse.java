package com.k955.kdex.dtos.Holding;

import java.math.BigDecimal;

public record HoldingResponse(
        String symbol,
        BigDecimal quantity,
        BigDecimal avgBuyPrice,
        BigDecimal currentPrice,
        BigDecimal currentValue,
        BigDecimal pnl,
        BigDecimal pnlPct
) {
}
