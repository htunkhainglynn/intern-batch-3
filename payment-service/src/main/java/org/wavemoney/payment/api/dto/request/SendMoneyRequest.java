package org.wavemoney.payment.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMoneyRequest(
        @NotBlank(message = "sender is required")
        String sender,

        @NotBlank(message = "receiver is required")
        String receiver,

        @NotNull(message = "amount is required")
        @Min(value = 101, message = "Amount must be greater than 100")
        Double amount
) {}