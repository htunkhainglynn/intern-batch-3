package org.wavemoney.payment.api.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wavemoney.payment.api.dto.request.SendMoneyRequest;
import org.wavemoney.payment.api.dto.response.SendMoneyResponse;
import org.wavemoney.payment.api.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/send-money")
    public ResponseEntity<SendMoneyResponse> sendMoney(@RequestBody @Valid SendMoneyRequest request) {
        SendMoneyResponse response = transactionService.sendMoney(request);
        return ResponseEntity.ok(response);
    }
}