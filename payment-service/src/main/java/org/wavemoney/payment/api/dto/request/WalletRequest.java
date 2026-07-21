package org.wavemoney.payment.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.wavemoney.payment.api.enums.Currency;

import java.math.BigDecimal;

@Builder
public record WalletRequest(

        @NotBlank(message = "Phone is required")
        String phone,

        @NotNull(message = "Balance is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
        BigDecimal balance,

        @NotNull(message = "Currency is required")
        Currency currency
) {
}