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
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody TransactionCreateDTO dto) {
        Transaction created = transactionService.createTransaction(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        return transactionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Transaction>> findByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(transactionService.findByProductId(productId));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> findAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }
}
