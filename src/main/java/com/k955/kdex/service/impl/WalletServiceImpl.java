package com.k955.kdex.service.impl;

import com.k955.kdex.dtos.Wallet.DepositRequest;
import com.k955.kdex.dtos.Wallet.DepositResponse;
import com.k955.kdex.dtos.Wallet.WalletResponse;
import com.k955.kdex.entity.Transaction;
import com.k955.kdex.entity.User;
import com.k955.kdex.entity.Wallet;
import com.k955.kdex.enums.Transaction.Type;
import com.k955.kdex.exception.BadRequestException;
import com.k955.kdex.exception.ErrorCode;
import com.k955.kdex.exception.ResourceNotFoundException;
import com.k955.kdex.mapper.WalletMapper;
import com.k955.kdex.repository.TransactionRepository;
import com.k955.kdex.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final WalletMapper walletMapper;

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
    @Transactional
    public DepositResponse deposit(UUID idempotencyKey, DepositRequest depositRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "We couldn't find your account. Please sign in again."
                ));

        if (depositRequest.amount() == null || depositRequest.amount().signum() <= 0) {
            throw new BadRequestException(
                    ErrorCode.BAD_REQUEST,
                    "Deposit amount must be greater than zero."
            );
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WALLET_NOT_FOUND, "We couldn't find your wallet. Please try again."
                ));

        boolean duplicateTransaction = transactionRepository.existsByUserIdAndIdempotencyKey(userId, idempotencyKey);
        if(duplicateTransaction) throw new BadRequestException(ErrorCode.DUPLICATE_REQUEST, "Your deposit requst was already initiated.");

        wallet.setBalance(wallet.getBalance().add(depositRequest.amount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(Type.DEPOSIT)
                .amount(depositRequest.amount())
                .balanceAfter(wallet.getBalance())
                .idempotencyKey(idempotencyKey)
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        return new DepositResponse(
                savedTransaction.getId(),
                savedTransaction.getType(),
                savedTransaction.getAmount(),
                savedTransaction.getBalanceAfter(),
                savedTransaction.getCreatedAt()
        );
    }

}
