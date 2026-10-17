package com.k955.kdex.service;

import com.k955.kdex.dtos.Wallet.DepositRequest;
import com.k955.kdex.dtos.Wallet.WalletResponse;
import com.k955.kdex.payment.CheckoutResponse;
import com.k955.kdex.payment.PortalResponse;
import jakarta.validation.Valid;

public interface WalletService {

    WalletResponse getUserWallet();

    CheckoutResponse createDepositCheckout(@Valid DepositRequest depositRequest);

    PortalResponse openCustomerPortal();
}
