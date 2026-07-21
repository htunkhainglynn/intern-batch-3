package org.wavemoney.payment.api.dto.response;

import java.util.Optional;

public interface WalletStatusProjection {
    Optional<String> getStatus();
}
