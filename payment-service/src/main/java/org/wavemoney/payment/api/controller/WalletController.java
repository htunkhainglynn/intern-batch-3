package org.wavemoney.payment.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wavemoney.payment.api.dto.request.WalletRequest;
import org.wavemoney.payment.api.dto.request.WalletStatusUpdateRequest;
import org.wavemoney.payment.api.dto.response.ApiResponse;
import org.wavemoney.payment.api.dto.response.WalletResponse;
import org.wavemoney.payment.api.enums.WalletStatus;
import org.wavemoney.payment.api.service.WalletService;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<ApiResponse> createWallet(
            @Valid @RequestBody WalletRequest request) {

        WalletResponse response = walletService.createWallet(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, HttpStatus.CREATED.value(), "Wallet created successfully"));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<ApiResponse> getWalletById(
            @PathVariable String walletId) {

        WalletResponse response = walletService.getWalletById(walletId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Wallet retrieved successfully")
        );
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse> getWalletByPhone(
            @PathVariable String phone) {

        WalletResponse response = walletService.getWalletByPhone(phone);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Wallet retrieved successfully")
        );
    }

    @GetMapping("/phone/{phone}/status")
    public ResponseEntity<?> getWalletStatusByPhone(
            @PathVariable String phone) {

        WalletStatus response = walletService.getWalletStatusByPhone(phone);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Wallet status retrieved successfully")
        );
    }

    @PutMapping("/{walletId}")
    public ResponseEntity<ApiResponse> updateWallet(
            @PathVariable String walletId,
            @Valid @RequestBody WalletRequest request) {

        WalletResponse response = walletService.updateWallet(walletId, request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Wallet updated successfully")
        );
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<ApiResponse> deleteWallet(
            @PathVariable String walletId) {

        walletService.deleteWallet(walletId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Wallet deleted successfully")
        );
    }

    @PatchMapping("/phone/{phone}/status")
    public ResponseEntity<WalletResponse> updateWalletStatus(
            @PathVariable String phone,
            @RequestBody @Valid WalletStatusUpdateRequest request) {

        WalletResponse response = walletService.updateWalletStatusByPhone(phone, request.walletStatus());
        return ResponseEntity.ok(response);
    }

}