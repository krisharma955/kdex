package com.k955.kdex.repository;

import com.k955.kdex.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    boolean existsByUserIdAndIdempotencyKey(UUID userId, UUID idempotencyKey);

    boolean existsByStripeSessionId(String stripeSessionId);

}
