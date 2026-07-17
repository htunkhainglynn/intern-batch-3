package org.wavemoney.payment.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {
    private String name;
    private String walletStatus;
    private String phone;
    private String level;
}

