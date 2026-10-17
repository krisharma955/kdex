package com.k955.kdex.service.impl;

import com.k955.kdex.dtos.Holding.HoldingResponse;
import com.k955.kdex.dtos.Transaction.TransactionResponse;
import com.k955.kdex.dtos.User.PortfolioResponse;
import com.k955.kdex.entity.Coin;
import com.k955.kdex.entity.Holding;
import com.k955.kdex.entity.Wallet;
import com.k955.kdex.enums.Transaction.Type;
import com.k955.kdex.exception.ErrorCode;
import com.k955.kdex.exception.ResourceNotFoundException;
import com.k955.kdex.mapper.TransactionMapper;
import com.k955.kdex.repository.CoinRepository;
import com.k955.kdex.repository.HoldingRepository;
import com.k955.kdex.repository.TransactionRepository;
import com.k955.kdex.repository.WalletRepository;
import com.k955.kdex.security.JwtAuthUtil;
import com.k955.kdex.service.UserService;
import com.k955.kdex.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtAuthUtil jwtAuthUtil;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final WalletRepository walletRepository;
    private final HoldingRepository holdingRepository;
    private final CoinRepository coinRepository;

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponse getUserPortfolio() {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WALLET_NOT_FOUND, "We couldn't find your wallet. Please try again."
                ));

        List<Holding> userHoldings = holdingRepository.findAllByUserId(userId);

        BigDecimal balance = wallet.getBalance();

        BigDecimal holdingValue = BigDecimal.ZERO;
        BigDecimal totalPnl = BigDecimal.ZERO;

        List<HoldingResponse> holdingResponses = new ArrayList<>();

        for(Holding holding : userHoldings) {
            Coin coin = holding.getCoin();

            BigDecimal quantity = holding.getQuantity();
            BigDecimal avgBuyPrice = holding.getAvgBuyPrice();
            BigDecimal currentPrice = coin.getCurrentPrice();

            // Current market value of this holding
            BigDecimal currentValue = quantity.multiply(currentPrice);

            // Total amount invested in this holding
            BigDecimal investedValue = quantity.multiply(avgBuyPrice);

            // Unrealized profit or loss
            BigDecimal pnl = currentValue.subtract(investedValue);

            // Profit/loss percentage
            BigDecimal pnlPct = investedValue.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : pnl.multiply(BigDecimal.valueOf(100))
                    .divide(investedValue, 2, RoundingMode.HALF_UP);

            holdingValue = holdingValue.add(currentValue);
            totalPnl = totalPnl.add(pnl);

            holdingResponses.add(
                    new HoldingResponse(
                            coin.getSymbol(),
                            quantity,
                            avgBuyPrice,
                            currentPrice,
                            currentValue.setScale(4, RoundingMode.HALF_UP),
                            pnl.setScale(4, RoundingMode.HALF_UP),
                            pnlPct
                    )
            );
        }

        BigDecimal totalValue = balance.add(holdingValue);

        return new PortfolioResponse(
                balance,
                holdingValue.setScale(4, RoundingMode.HALF_UP),
                totalValue.setScale(4, RoundingMode.HALF_UP),
                totalPnl.setScale(4, RoundingMode.HALF_UP),
                holdingResponses
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactions(
            String symbol, Type type, Instant from, Instant to, Pageable pageable)
    {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        return transactionRepository.findAll(
                TransactionSpecification.filterBy(userId, symbol, type, from, to), pageable
        ).map(transactionMapper::toTransactionResponse);
    }

}
