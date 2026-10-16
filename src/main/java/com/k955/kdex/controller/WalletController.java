package com.k955.kdex.controller;

import com.k955.kdex.dtos.Wallet.DepositRequest;
import com.k955.kdex.dtos.Wallet.DepositResponse;
import com.k955.kdex.dtos.Wallet.WalletResponse;
import com.k955.kdex.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    @GetMapping()
    public ResponseEntity<WalletResponse> getUserWallet() {
        return ResponseEntity.ok(walletService.getUserWallet());
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @Valid @RequestBody DepositRequest depositRequest
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(walletService.deposit(idempotencyKey, depositRequest));
    }

}
