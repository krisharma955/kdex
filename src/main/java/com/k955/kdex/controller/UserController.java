package com.k955.kdex.controller;

import com.k955.kdex.dtos.Trade.TradeResponse;
import com.k955.kdex.dtos.Transaction.TransactionResponse;
import com.k955.kdex.dtos.User.PortfolioResponse;
import com.k955.kdex.enums.Transaction.Type;
import com.k955.kdex.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioResponse> getUserPortfolio() {
        return ResponseEntity.ok(userService.getUserPortfolio());
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionResponse>> getUserTransactions(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getUserTransactions(symbol, type, from, to, pageable));
    }

}
