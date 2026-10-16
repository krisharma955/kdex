package com.k955.kdex.dtos.Trade;

import com.k955.kdex.enums.Transaction.Type;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponse(
        UUID transactionId,
        Type type,
        String symbol,
        BigDecimal quantity,
        BigDecimal executionPrice,
        BigDecimal totalAmount,
        BigDecimal balanceAfter,
        Instant createdAt
) {
}
