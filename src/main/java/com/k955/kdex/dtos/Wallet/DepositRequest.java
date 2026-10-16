package com.k955.kdex.dtos.Wallet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(

        @NotNull(message = "Deposit amount is required")
        @DecimalMin(
                value = "0.0001",
                message = "Deposit amount must be greater than zero"
        )
        @Digits(
                integer = 19,
                fraction = 4,
                message = "Deposit amount can have at most 4 decimal places"
        )
        BigDecimal amount

) {
}
