package gabs.application.ports;

import gabs.application.dto.TransactionCreateDTO;
import gabs.domain.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionUseCases {
    Transaction createTransaction(TransactionCreateDTO dto);
    Optional<Transaction> findById(Long id);


    List<Transaction> findByProductAccountNumber(String productAccountNumber);

    List<Transaction> findAll();
}
