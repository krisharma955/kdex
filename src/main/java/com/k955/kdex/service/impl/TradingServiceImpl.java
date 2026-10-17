package com.k955.kdex.service.impl;

import com.k955.kdex.dtos.Trade.BuyRequest;
import com.k955.kdex.dtos.Trade.SellRequest;
import com.k955.kdex.dtos.Trade.TradeResponse;
import com.k955.kdex.entity.*;
import com.k955.kdex.enums.Transaction.Type;
import com.k955.kdex.exception.BadRequestException;
import com.k955.kdex.exception.ErrorCode;
import com.k955.kdex.exception.ResourceNotFoundException;
import com.k955.kdex.repository.*;
import com.k955.kdex.security.JwtAuthUtil;
import com.k955.kdex.service.TradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;
    private final HoldingRepository holdingRepository;

    @Override
    @Transactional
    public TradeResponse buy(UUID idempotencyKey, BuyRequest buyRequest) {
        if (buyRequest.quantity() == null || buyRequest.quantity().signum() <= 0) {
            throw new BadRequestException(
                    ErrorCode.BAD_REQUEST,
                    "Quantity must be greater than zero."
            );
        }

        UUID userId = jwtAuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Later!"
                ));

        boolean duplicateTransaction = transactionRepository.existsByUserIdAndIdempotencyKey(userId, idempotencyKey);
        if(duplicateTransaction) throw new BadRequestException(
                ErrorCode.DUPLICATE_REQUEST, "Your buy requst was already initiated.");

        Coin coin = coinRepository.findBySymbol(buyRequest.symbol().trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.COIN_NOT_FOUND, "The coin can't be loaded at the moment. Try Later!"
                ));

        BigDecimal currentCoinPrice = coin.getCurrentPrice();

        BigDecimal totalAmount = currentCoinPrice.multiply(buyRequest.quantity());

        if(!coin.getIsActive().equals(true)) {
            throw new BadRequestException(ErrorCode.COIN_INACTIVE, "The coin can't be traded at the moment.");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WALLET_NOT_FOUND, "We couldn't find your wallet. Please try again."
                ));

        if(wallet.getBalance().compareTo(totalAmount) < 0) {
            throw new BadRequestException(ErrorCode.INSUFFICIENT_BALANCE, "You don't have sufficient funds to proceed with the order.");
        }

        BigDecimal newBalance = wallet.getBalance().subtract(totalAmount);

        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        boolean holdingExistence = holdingRepository.existsByUserIdAndCoinId(user.getId(), coin.getId());
        if(holdingExistence) {
            Holding holding = holdingRepository.findByUserIdAndCoinId(user.getId(), coin.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ErrorCode.HOLDING_NOT_FOUND, "Your holding can't be loaded at the moment. Please try again!"
                    ));

            BigDecimal newQuantity = holding.getQuantity().add(buyRequest.quantity());

            BigDecimal oldCost = holding.getQuantity()
                    .multiply(holding.getAvgBuyPrice());

            BigDecimal newTotalCost = oldCost.add(totalAmount);

            BigDecimal newAvgBuyPrice = newTotalCost.divide(
                    newQuantity, 8, RoundingMode.HALF_UP
            );

            holding.setQuantity(newQuantity);
            holding.setAvgBuyPrice(newAvgBuyPrice);
            holdingRepository.save(holding);
        }
        else {
            BigDecimal avgBuyPrice = totalAmount.divide(
                    buyRequest.quantity(), 8, RoundingMode.HALF_UP
            );

            Holding holding = Holding.builder()
                    .user(user)
                    .coin(coin)
                    .quantity(buyRequest.quantity())
                    .avgBuyPrice(avgBuyPrice)
                    .build();
            holdingRepository.save(holding);
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(Type.BUY)
                .coin(coin)
                .quantity(buyRequest.quantity())
                .price(currentCoinPrice)
                .amount(totalAmount)
                .balanceAfter(newBalance)
                .idempotencyKey(idempotencyKey)
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TradeResponse(
                savedTransaction.getId(),
                savedTransaction.getType(),
                coin.getSymbol(),
                savedTransaction.getQuantity(),
                currentCoinPrice,
                totalAmount,
                savedTransaction.getBalanceAfter(),
                savedTransaction.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public TradeResponse sell(UUID idempotencyKey, SellRequest sellRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Later!"
                ));

        boolean duplicateTransaction = transactionRepository.existsByUserIdAndIdempotencyKey(userId, idempotencyKey);
        if(duplicateTransaction) throw new BadRequestException(
                ErrorCode.DUPLICATE_REQUEST, "Your sell requst was already initiated.");

        Coin coin = coinRepository.findBySymbol(sellRequest.symbol().trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.COIN_NOT_FOUND, "The coin can't be loaded at the moment. Try Later!"
                ));

        BigDecimal currentCoinPrice = coin.getCurrentPrice();

        BigDecimal totalAmount = currentCoinPrice.multiply(sellRequest.quantity());

        if(!coin.getIsActive().equals(true)) {
            throw new BadRequestException(ErrorCode.COIN_INACTIVE, "The coin can't be traded at the moment.");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WALLET_NOT_FOUND, "We couldn't find your wallet. Please try again."
                ));

        Holding holding = holdingRepository.findByUserIdAndCoinId(user.getId(), coin.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.HOLDING_NOT_FOUND, "Your holding can't be loaded at the moment. Please try again!"
                ));

        if(holding.getQuantity().compareTo(sellRequest.quantity()) < 0) {
            throw new BadRequestException(ErrorCode.INSUFFICIENT_HOLDINGS, "You don't have sufficient holdings to proceed with the order.");
        }

        BigDecimal newBalance = wallet.getBalance().add(totalAmount);

        BigDecimal newQuantity = holding.getQuantity().subtract(sellRequest.quantity());

        holding.setQuantity(newQuantity);
        holdingRepository.save(holding);

        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(Type.SELL)
                .coin(coin)
                .quantity(sellRequest.quantity())
                .price(currentCoinPrice)
                .amount(totalAmount)
                .balanceAfter(newBalance)
                .idempotencyKey(idempotencyKey)
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TradeResponse(
                savedTransaction.getId(),
                savedTransaction.getType(),
                coin.getSymbol(),
                savedTransaction.getQuantity(),
                currentCoinPrice,
                totalAmount,
                savedTransaction.getBalanceAfter(),
                savedTransaction.getCreatedAt()
        );
    }
}
