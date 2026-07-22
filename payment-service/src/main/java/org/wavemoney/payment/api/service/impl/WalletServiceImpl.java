package org.wavemoney.payment.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.request.WalletRequest;
import org.wavemoney.payment.api.dto.response.WalletResponse;
import org.wavemoney.payment.api.entity.Wallet;
import org.wavemoney.payment.api.enums.WalletStatus;
import org.wavemoney.payment.api.repository.WalletRepository;
import org.wavemoney.payment.api.service.WalletService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public WalletResponse createWallet(WalletRequest request) {

        Wallet wallet = Wallet.builder()
                .phone(request.phone())
                .balance(BigDecimal.ZERO)
                .currency(request.currency())
                .walletStatus(WalletStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        Wallet savedWallet = walletRepository.save(wallet);

        return mapToResponse(savedWallet);
    }

    @Override
    public WalletResponse getWalletById(String walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return mapToResponse(wallet);
    }

    @Override
    public WalletResponse getWalletByPhone(String phone) {

        Wallet wallet = walletRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return mapToResponse(wallet);
    }

    @Override
    public WalletResponse updateWallet(String walletId, WalletRequest request) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setPhone(request.phone());
        wallet.setCurrency(request.currency());

        Wallet updatedWallet = walletRepository.save(wallet);

        return mapToResponse(updatedWallet);
    }

    @Override
    public void deleteWallet(String walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        walletRepository.delete(wallet);
    }

    @Override
    public WalletStatus getWalletStatusByPhone(String phone){
        Wallet wallet = walletRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return wallet.getWalletStatus();
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .phone(wallet.getPhone())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .walletStatus(wallet.getWalletStatus())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdateAt())
                .build();
    }
}