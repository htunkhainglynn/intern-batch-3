package org.wavemoney.payment.api.service;

import org.wavemoney.payment.api.dto.request.SendMoneyRequest;
import org.wavemoney.payment.api.dto.response.SendMoneyResponse;

public interface TransactionService {
    SendMoneyResponse sendMoney(SendMoneyRequest request);
}