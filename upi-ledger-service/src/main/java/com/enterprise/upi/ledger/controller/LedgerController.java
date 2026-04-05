package com.enterprise.upi.ledger.controller;

import com.enterprise.upi.common.dto.GenericResponse;
import com.enterprise.upi.ledger.model.TransactionEntity;
import com.enterprise.upi.ledger.repository.TransactionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ledger")
public class LedgerController {

    private final TransactionRepository transactionRepository;

    public LedgerController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/transactions")
    public GenericResponse<List<TransactionEntity>> getRecentTransactions() {
        // Fetch last 10 transactions
        List<TransactionEntity> transactions = transactionRepository.findAll(
            Sort.by(Sort.Direction.DESC, "eventTimestamp")
        );
        return GenericResponse.success(transactions);
    }
}
