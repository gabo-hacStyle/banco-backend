package gabs.infrastructure.controller;


import gabs.application.dto.TransactionCreateDTO;
import gabs.application.ports.TransactionUseCases;
import gabs.application.service.TransactionService;
import gabs.domain.entity.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionUseCases transactionService;
    public TransactionController(TransactionUseCases transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Transaction create(@RequestBody TransactionCreateDTO dto) {


        return transactionService.createTransaction(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        Transaction transaction = transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/product/{productAccountNumber}")
    public ResponseEntity<List<Transaction>> findByProduct(@PathVariable String productAccountNumber) {
        return ResponseEntity.ok(transactionService.findByProductAccountNumber(productAccountNumber));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> findAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }
}
