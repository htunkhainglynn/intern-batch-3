package org.wavemoney.payment.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wavemoney.payment.api.dto.request.SendMoneyRequest;
import org.wavemoney.payment.api.dto.response.SendMoneyResponse;
import org.wavemoney.payment.api.entity.User;
import org.wavemoney.payment.api.entity.Wallet;
import org.wavemoney.payment.api.entity.Transaction;
import org.wavemoney.payment.api.enums.TransactionStatus;
import org.wavemoney.payment.api.enums.TransactionType;
import org.wavemoney.payment.api.enums.WalletStatus;
import org.wavemoney.payment.api.exception.BusinessLogicException;
import org.wavemoney.payment.api.repository.UserRepository;
import org.wavemoney.payment.api.repository.WalletRepository;
import org.wavemoney.payment.api.repository.TransactionRepository;
import org.wavemoney.payment.api.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Value("${transaction-amount.min}")
    private double minimumTransactionAmount;

    @Value("${wallet-limit}")
    private double walletLimit;

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate kafkaTemplate;

    @Override
    public SendMoneyResponse sendMoney(SendMoneyRequest request) {
        log.info("Request received: {}", request);
        // Validate all 6 conditions & retrieve entities
        ValidatedTransferData transferData = validateSendMoneyTransaction(request);

        Wallet senderWallet = transferData.senderWallet();
        Wallet receiverWallet = transferData.receiverWallet();
        BigDecimal transferAmount = BigDecimal.valueOf(request.amount());

        // Perform balance transfer
        senderWallet.setBalance(senderWallet.getBalance().subtract(transferAmount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(transferAmount));

        log.info("Sender Wallet: {}, Receiver Wallet: {}", senderWallet, receiverWallet);
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // Record the transaction
        Transaction transaction = Transaction.builder()
                .sender(request.sender())
                .receiver(request.receiver())
                .amount(request.amount())
                .transactionType(TransactionType.SendMoney)
                .datetime(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Transaction saved: {}", savedTransaction);

        log.info("Sending transaction event to Kafka: {}", savedTransaction);
        kafkaTemplate.send("transaction-events", savedTransaction);

        return SendMoneyResponse.builder()
                .transactionId(savedTransaction.getTransactionId())
                .sender(savedTransaction.getSender())
                .receiver(savedTransaction.getReceiver())
                .amount(savedTransaction.getAmount())
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactionType(savedTransaction.getTransactionType())
                .dateTime(savedTransaction.getDatetime())
                .build();
    }

    /**
     * Validates all business requirements before processing money transfer.
     */
    private ValidatedTransferData validateSendMoneyTransaction(SendMoneyRequest request) {

        // Rule 6: Amount must be > 100
        if (request.amount() == null || request.amount() <= minimumTransactionAmount) {
            throw BusinessLogicException.business("INVALID_AMOUNT", "Transaction amount must be greater than " + minimumTransactionAmount);
        }

        // Rule 1: Check receiver account exists or not (also check sender existence)
        User senderUser = userRepository.findByPhone(request.sender())
                .orElseThrow(() -> BusinessLogicException.notFound("SENDER_NOT_FOUND", "Sender account does not exist."));

        User receiverUser = userRepository.findByPhone(request.receiver())
                .orElseThrow(() -> BusinessLogicException.notFound("RECEIVER_NOT_FOUND", "Receiver account does not exist."));

        Wallet senderWallet = walletRepository.findByPhone(request.sender())
                .orElseThrow(() -> BusinessLogicException.notFound("SENDER_WALLET_NOT_FOUND", "Sender wallet not found."));

        Wallet receiverWallet = walletRepository.findByPhone(request.receiver())
                .orElseThrow(() -> BusinessLogicException.notFound("RECEIVER_WALLET_NOT_FOUND", "Receiver wallet not found."));

        // Rule 2: Sender account is Level 2 and WalletStatus ACTIVE
        if (!"2".equals(senderUser.getLevel())) {
            throw BusinessLogicException.business("SENDER_NOT_LEVEL_2", "Sender account must be Level 2.");
        }
        if (senderWallet.getWalletStatus() != WalletStatus.ACTIVE) {
            throw BusinessLogicException.business("SENDER_WALLET_INACTIVE", "Sender wallet is not ACTIVE.");
        }

        // Rule 3: Receiver account is Level 2 and WalletStatus ACTIVE
        if (!"2".equals(receiverUser.getLevel())) {
            throw BusinessLogicException.business("RECEIVER_NOT_LEVEL_2", "Receiver account must be Level 2.");
        }
        if (receiverWallet.getWalletStatus() != WalletStatus.ACTIVE) {
            throw BusinessLogicException.business("RECEIVER_WALLET_INACTIVE", "Receiver wallet is not ACTIVE.");
        }

        // Rule 4: Sender balance is enough
        BigDecimal transferAmount = BigDecimal.valueOf(request.amount());
        if (senderWallet.getBalance().compareTo(transferAmount) < 0) {
            throw BusinessLogicException.business("INSUFFICIENT_BALANCE", "Sender does not have sufficient balance.");
        }

        // Rule 5: Receiver balance hit limit (maximum 100,000)
        BigDecimal projectedReceiverBalance = receiverWallet.getBalance().add(transferAmount);
        if (projectedReceiverBalance.compareTo(BigDecimal.valueOf(walletLimit)) > 0) {
            throw BusinessLogicException.business("RECEIVER_LIMIT_EXCEEDED", "Transaction would cause receiver balance to exceed limit of " + walletLimit);
        }

        return new ValidatedTransferData(senderWallet, receiverWallet);
    }

    // Helper holder for validated wallets
    private record ValidatedTransferData(Wallet senderWallet, Wallet receiverWallet) {}
}