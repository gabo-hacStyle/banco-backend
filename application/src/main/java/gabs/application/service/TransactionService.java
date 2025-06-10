package gabs.application.service;


import gabs.application.dto.TransactionCreateDTO;
import gabs.application.ports.TransactionUseCases;
import gabs.domain.entity.Product;
import gabs.domain.entity.Transaction;
import gabs.domain.ports.ProductRepository;
import gabs.domain.ports.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements TransactionUseCases {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    public TransactionService(TransactionRepository transactionRepository, ProductRepository productRepository) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Transaction createTransaction(TransactionCreateDTO dto) {
        Transaction.Type type = Transaction.Type.valueOf(dto.getType());
        BigDecimal amount = dto.getAmount();

        // Validar productos
        Product source = null;
        Product target = null;

        if (type == Transaction.Type.DEPOSIT) {
            target = productRepository.findById(dto.getTargetProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no existe"));

            target.updateBalance(target.getBalance().add(amount));
            productRepository.save(target);

            return transactionRepository.save(
                    new Transaction(null, type, amount, null, Transaction.Status.SUCCESS, null, target.getId(), dto.getDescription())
            );
        }

        if (type == Transaction.Type.WITHDRAWAL) {
            source = productRepository.findById(dto.getSourceProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no existe"));
            if (source.getBalance().compareTo(amount) < 0)
                throw new IllegalArgumentException("Saldo insuficiente para retiro");

            source.updateBalance(source.getBalance().subtract(amount));
            productRepository.save(source);

            return transactionRepository.save(
                    new Transaction(null, type, amount, null, Transaction.Status.SUCCESS, source.getId(), null, dto.getDescription())
            );
        }

        if (type == Transaction.Type.TRANSFER) {
            source = productRepository.findById(dto.getSourceProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no existe"));
            target = productRepository.findById(dto.getTargetProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no existe"));
            if (source.getBalance().compareTo(amount) < 0)
                throw new IllegalArgumentException("Saldo insuficiente para transferir");

            source.updateBalance(source.getBalance().subtract(amount));
            target.updateBalance(target.getBalance().add(amount));
            productRepository.save(source);
            productRepository.save(target);

            // Genera dos movimientos: débito y crédito
            transactionRepository.save(
                    new Transaction(null, type, amount.negate(), null, Transaction.Status.SUCCESS, source.getId(), target.getId(), "Débito transferencia: " + dto.getDescription())
            );
            return transactionRepository.save(
                    new Transaction(null, type, amount, null, Transaction.Status.SUCCESS, source.getId(), target.getId(), "Crédito transferencia: " + dto.getDescription())
            );
        }

        throw new IllegalArgumentException("Tipo de transacción no soportado");
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);

    }
    @Override
    public List<Transaction> findByProductId(Long productId) {
        return transactionRepository.findByProductId(productId);
    }
    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }


}
