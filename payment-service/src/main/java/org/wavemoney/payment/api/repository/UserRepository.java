package org.wavemoney.payment.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.wavemoney.payment.api.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

}
