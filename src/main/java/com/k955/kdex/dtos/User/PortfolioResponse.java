package com.k955.kdex.dtos.User;

import com.k955.kdex.dtos.Holding.HoldingResponse;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioResponse(
        BigDecimal balance,
        BigDecimal holdingValue,
        BigDecimal totalValue, //holdingValue + balance
        BigDecimal totalPnl,
        List<HoldingResponse> holdings
) {
}
