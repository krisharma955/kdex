package com.k955.kdex.service.impl;

import com.k955.kdex.dtos.Coin.MarketPriceUpdate;
import com.k955.kdex.entity.Coin;
import com.k955.kdex.entity.PriceHistory;
import com.k955.kdex.repository.CoinRepository;
import com.k955.kdex.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketEngineService {

    private final CoinRepository coinRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final int PRICE_SCALE = 8;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal MIN_PRICE = new BigDecimal("0.00000001");

    @Transactional
    @Scheduled(fixedRate = 10_000)
    public void updateMarketPrices() {
        List<Coin> coins = coinRepository.findByIsActiveTrue();

        if (coins.isEmpty()) {
            return;
        }

        Instant tickTime = Instant.now(); // same timestamp for every coin in this tick
        List<PriceHistory> history = new ArrayList<>(coins.size());

        for(Coin coin : coins) {
            BigDecimal oldPrice = coin.getCurrentPrice();

            BigDecimal newPrice = calculateNewPrice(oldPrice, coin.getVolatilityPct());

            coin.setPreviousPrice(oldPrice);
            coin.setCurrentPrice(newPrice);
            coin.setUpdatedAt(tickTime);

            history.add(PriceHistory.builder()
                    .coin(coin)
                    .price(newPrice)
                    .recordedAt(tickTime)
                    .build());

            BigDecimal changePct = newPrice
                    .subtract(oldPrice)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(oldPrice, 2, RoundingMode.HALF_UP);

            applicationEventPublisher.publishEvent(
                    new MarketPriceUpdate(
                            coin.getSymbol(),
                            oldPrice,
                            newPrice,
                            changePct,
                            tickTime
                    )
            );
        }

        coinRepository.saveAll(coins);
        priceHistoryRepository.saveAll(history);
        log.debug("Market tick: updated {} coins", coins.size());
    }

    /**
     * Random walk: newPrice = oldPrice * (1 + r * volatilityPct / 100),
     * where r is uniformly random in [-1, 1). volatilityPct of 0.5 means at most +-0.5% per tick.
     */
    private BigDecimal calculateNewPrice(BigDecimal oldPrice, BigDecimal volatilityPct) {
        double r = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);

        BigDecimal changePct = volatilityPct.multiply(BigDecimal.valueOf(r));
        BigDecimal factor = BigDecimal.ONE.add(
                changePct.divide(HUNDRED, 10, RoundingMode.HALF_UP));

        BigDecimal newPrice = oldPrice.multiply(factor)
                .setScale(PRICE_SCALE, RoundingMode.HALF_UP);

        return newPrice.max(MIN_PRICE);
    }

}
