package org.wavemoney.payment.api.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.wavemoney.payment.api.enums.Currency;
import org.wavemoney.payment.api.enums.WalletStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wallet")
public class Wallet {

    @Id
    private String walletId;

    private String phone;

    private BigDecimal balance;

    private Currency currency;

    private WalletStatus walletStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;


}
