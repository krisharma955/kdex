package com.k955.kdex.dtos.Coin;

import jakarta.validation.constraints.NotNull;

public record UpdateCoinStatusRequest(
        @NotNull boolean isActive
) {
}
