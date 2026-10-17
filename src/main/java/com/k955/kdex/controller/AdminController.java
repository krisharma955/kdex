package com.k955.kdex.controller;

import com.k955.kdex.dtos.Coin.CoinRequest;
import com.k955.kdex.dtos.Coin.CoinResponse;
import com.k955.kdex.dtos.Coin.UpdateCoinRequest;
import com.k955.kdex.dtos.Coin.UpdateCoinStatusRequest;
import com.k955.kdex.service.CoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final CoinService coinService;

    /// COIN

    @PostMapping("/new-coin")
    public ResponseEntity<CoinResponse> createCoin(@Valid @RequestBody CoinRequest coinRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coinService.createCoin(coinRequest));
    }

    @PatchMapping("/{coinId}")
    public ResponseEntity<CoinResponse> updateCoinById(
            @PathVariable UUID coinId, @Valid @RequestBody UpdateCoinRequest updateCoinRequest
    ) {
        return ResponseEntity.ok(coinService.updateCoinById(coinId, updateCoinRequest));
    }

    @PatchMapping("/{coinId}/status")
    public ResponseEntity<CoinResponse> updateCoinStatus(
            @PathVariable UUID coinId, @Valid @RequestBody UpdateCoinStatusRequest updateCoinStatusRequest
    ) {
        return ResponseEntity.ok(coinService.updateCoinStatus(coinId, updateCoinStatusRequest));
    }

}
