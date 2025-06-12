package gabs.domain.ports;

import gabs.domain.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findByProductAccountNumber(String productAccountNumber);
    List<Transaction> findAll();
}
