package com.k955.kdex.service;

import com.k955.kdex.dtos.Trade.BuyRequest;
import com.k955.kdex.dtos.Trade.SellRequest;
import com.k955.kdex.dtos.Trade.TradeResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface TradingService {

    TradeResponse buy(UUID idempotencyKey, @Valid BuyRequest buyRequest);

    TradeResponse sell(UUID idempotencyKey, @Valid SellRequest sellRequest);

}
