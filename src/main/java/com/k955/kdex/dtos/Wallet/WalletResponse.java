package com.k955.kdex.dtos.Wallet;

import java.math.BigDecimal;
import java.time.Instant;

public record WalletResponse(
        BigDecimal balance,
        Instant updatedAt
) {
}
