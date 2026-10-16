package com.k955.kdex.dtos.Wallet;

import com.k955.kdex.enums.Transaction.Type;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DepositResponse(
        UUID transactionId,
        Type type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        Instant depositedAt
) {
}
