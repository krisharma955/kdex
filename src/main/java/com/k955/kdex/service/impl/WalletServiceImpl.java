package com.k955.kdex.service.impl;

import com.k955.kdex.dtos.Wallet.DepositRequest;
import com.k955.kdex.dtos.Wallet.WalletResponse;
import com.k955.kdex.entity.Wallet;
import com.k955.kdex.exception.ErrorCode;
import com.k955.kdex.exception.ResourceNotFoundException;
import com.k955.kdex.mapper.WalletMapper;
import com.k955.kdex.payment.CheckoutResponse;
import com.k955.kdex.payment.PaymentProcessor;
import com.k955.kdex.repository.WalletRepository;
import com.k955.kdex.security.JwtAuthUtil;
import com.k955.kdex.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final WalletMapper walletMapper;
    private final PaymentProcessor paymentProcessor;

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getUserWallet() {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WALLET_NOT_FOUND, "We couldn't find your wallet. Please try again."
                ));

        return walletMapper.toWalletResponse(wallet);
    }

    @Override
    public CheckoutResponse createDepositCheckout(DepositRequest depositRequest) {
        return paymentProcessor.createCheckoutSessionUrl(depositRequest);
    }

}
