package com.k955.kdex.service;

import com.k955.kdex.dtos.Coin.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CoinService {

    Page<CoinResponse> getCoins(Pageable pageable);

    CoinResponse getCoinBySymbol(String symbol);

    CoinHistory getCoinHistory(String symbol);

    CoinResponse updateCoinById(UUID coinId, @Valid UpdateCoinRequest updateCoinRequest);

    CoinResponse createCoin(@Valid CoinRequest coinRequest);

    CoinResponse updateCoinStatus(UUID coinId, @Valid UpdateCoinStatusRequest updateCoinStatusRequest);
}
