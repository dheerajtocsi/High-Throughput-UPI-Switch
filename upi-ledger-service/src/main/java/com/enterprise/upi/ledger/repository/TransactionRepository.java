package com.enterprise.upi.ledger.repository;

import com.enterprise.upi.ledger.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
}
