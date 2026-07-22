package org.wavemoney.payment.api.repository;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.wavemoney.payment.api.entity.Transaction;

public interface TransactionRepository extends MongoRepository<@NonNull Transaction, @NonNull String> {
}
