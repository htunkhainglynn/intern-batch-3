package org.wavemoney.payment.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.wavemoney.payment.api.entity.Wallet;
import org.wavemoney.payment.api.enums.WalletStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends MongoRepository<Wallet, String> {

    List<Wallet> findAllByPhone(String phone);

    boolean existsByPhone(String phone);

    Optional<Wallet> findByPhone(String phone);

}