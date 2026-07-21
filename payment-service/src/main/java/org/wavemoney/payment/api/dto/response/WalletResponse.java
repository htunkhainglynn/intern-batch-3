package org.wavemoney.payment.api.dto.response;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.wavemoney.payment.api.enums.Currency;
import org.wavemoney.payment.api.enums.WalletStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record WalletResponse(

        String walletId,

        String phone,

        BigDecimal balance,

        Currency currency,

        WalletStatus walletStatus,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}