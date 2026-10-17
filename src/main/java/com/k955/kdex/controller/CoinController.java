package com.k955.kdex.controller;

import com.k955.kdex.dtos.Coin.*;
import com.k955.kdex.service.CoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coins")
public class CoinController {

    private final CoinService coinService;

    @GetMapping
    public ResponseEntity<Page<CoinResponse>> getCoins(
            @PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(coinService.getCoins(pageable));
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<CoinResponse> getCoinBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(coinService.getCoinBySymbol(symbol));
    }

    @GetMapping("/{symbol}/history")
    public ResponseEntity<CoinHistory> getCoinHistory(@PathVariable String symbol) {
        return ResponseEntity.ok(coinService.getCoinHistory(symbol));
    }

}
