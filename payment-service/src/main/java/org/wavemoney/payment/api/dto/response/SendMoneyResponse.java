package org.wavemoney.payment.api.dto.response;

import lombok.Builder;
import org.wavemoney.payment.api.enums.TransactionStatus;
import org.wavemoney.payment.api.enums.TransactionType;

import java.time.LocalDateTime;

@Builder
public record SendMoneyResponse (
        String transactionId,
        String sender,
        String receiver,
        Double amount,
        TransactionStatus transactionStatus,
        TransactionType transactionType,
        LocalDateTime dateTime
) {

}
