package com.k955.kdex.controller;

import com.k955.kdex.dtos.Wallet.DepositRequest;
import com.k955.kdex.dtos.Wallet.WalletResponse;
import com.k955.kdex.payment.CheckoutResponse;
import com.k955.kdex.payment.PortalResponse;
import com.k955.kdex.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CheckoutResponse> deposit(@Valid @RequestBody DepositRequest depositRequest) {
        return ResponseEntity.ok(walletService.createDepositCheckout(depositRequest));
    }

    @PostMapping("/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal() {
        return ResponseEntity.ok(walletService.openCustomerPortal());
    }

}
