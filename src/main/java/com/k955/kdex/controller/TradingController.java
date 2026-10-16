package com.k955.kdex.controller;

import com.k955.kdex.dtos.Trade.BuyRequest;
import com.k955.kdex.dtos.Trade.SellRequest;
import com.k955.kdex.dtos.Trade.TradeResponse;
import com.k955.kdex.service.TradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trades")
public class TradingController {

    private final TradingService tradingService;

    @PostMapping("/buy")
    public ResponseEntity<TradeResponse> buy(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @Valid @RequestBody BuyRequest buyRequest
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(tradingService.buy(idempotencyKey, buyRequest));
    }

    @PostMapping("/sell")
    public ResponseEntity<TradeResponse> sell(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @Valid @RequestBody SellRequest sellRequest
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(tradingService.sell(idempotencyKey, sellRequest));
    }

}
