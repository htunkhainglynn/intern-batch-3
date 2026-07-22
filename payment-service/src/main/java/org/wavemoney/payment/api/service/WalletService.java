package org.wavemoney.payment.api.service;

import org.wavemoney.payment.api.dto.request.WalletRequest;
import org.wavemoney.payment.api.dto.response.WalletResponse;
import org.wavemoney.payment.api.enums.WalletStatus;

import java.util.List;

public interface WalletService {

    WalletResponse createWallet(WalletRequest request);

    WalletResponse getWalletById(String walletId);

    List<WalletResponse> getWalletsByPhone(String phone);

    WalletResponse updateWallet(String walletId, WalletRequest request);

    void deleteWallet(String walletId);

    WalletStatus getWalletStatusByPhone(String phone);

    WalletResponse updateWalletStatusByPhone(String phone, WalletStatus status);
}