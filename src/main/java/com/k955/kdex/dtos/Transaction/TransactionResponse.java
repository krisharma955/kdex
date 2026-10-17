package com.k955.kdex.dtos.Transaction;

import com.k955.kdex.enums.Transaction.Type;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        Type type,
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal amount,
        BigDecimal balanceAfter,
        Instant createdAt
) {
}
