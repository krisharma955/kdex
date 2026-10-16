package com.k955.kdex.service;

import com.k955.kdex.dtos.Wallet.DepositRequest;
import com.k955.kdex.dtos.Wallet.DepositResponse;
import com.k955.kdex.dtos.Wallet.WalletResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface WalletService {

    WalletResponse getUserWallet();

    DepositResponse deposit(UUID idempotencyKey, @Valid DepositRequest depositRequest);
}
