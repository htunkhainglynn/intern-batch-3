package org.wavemoney.payment.api.dto.response;

import lombok.Builder;
import lombok.Data;
import org.wavemoney.payment.api.enums.WalletStatus;

@Builder
@Data
public class UserResponse {
    private String name;
    private WalletStatus walletStatus;
    private String phone;
    private String level;
}

