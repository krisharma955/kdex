package com.k955.kdex.bootstrap;

import com.k955.kdex.entity.Coin;
import com.k955.kdex.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoinInitializer implements ApplicationRunner {

    private final CoinRepository coinRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedCoin("BTC",  "Bitcoin",  "110000", "0.15");
        seedCoin("ETH",  "Ethereum", "4000",   "0.25");
        seedCoin("SOL",  "Solana",   "200",    "0.40");
        seedCoin("XRP",  "XRP",      "2.50",   "0.35");
        seedCoin("DOGE", "Dogecoin", "0.20",   "0.60");
        log.info("Initial Coins Seeded Successfully.");
    }

    private void seedCoin(
            String symbol,
            String name,
            String price,
            String volatility
    ) {
        if (coinRepository.existsBySymbol(symbol)) {
            return;
        }

        BigDecimal initialPrice = new BigDecimal(price);

        Coin coin = Coin.builder()
                .symbol(symbol)
                .name(name)
                .currentPrice(initialPrice)
                .previousPrice(initialPrice)
                .volatilityPct(new BigDecimal(volatility))
                .isActive(true)
                .build();

        coinRepository.save(coin);
    }

}
