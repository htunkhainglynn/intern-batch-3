package org.wavemoney.payment.api.dto.request;

import jakarta.validation.constraints.NotNull;
import org.wavemoney.payment.api.enums.WalletStatus;

public record WalletStatusUpdateRequest(
        @NotNull(message = "Wallet status is required")
        WalletStatus walletStatus
) {}