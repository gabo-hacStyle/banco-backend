package gabs.application.service;


import gabs.application.dto.TransactionCreateDTO;
import gabs.application.ports.TransactionUseCases;
import gabs.domain.entity.Product;
import gabs.domain.entity.Transaction;
import gabs.domain.exceptions.NotFoundException;
import gabs.domain.ports.ProductRepository;
import gabs.domain.ports.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        BigDecimal amount = dto.getAmount();

        Product source = null;
        Product target = null;




        if (String.valueOf(Transaction.Type.DEPOSIT).equalsIgnoreCase(dto.getType())) {

            Transaction.Type type = Transaction.Type.valueOf(dto.getType());
            target = productRepository.findByAccountNumber(dto.getTargetProductId())
                    .orElseThrow(() -> new NotFoundException("Cuenta destino no existe"));

            if (target.getStatus() != Product.Status.ACTIVE) {
                throw new IllegalArgumentException("No se puede hacer depósito a una cuenta inactiva o cancelada");
            }

            target.updateBalance(target.getBalance().add(amount));
            productRepository.save(target);

            return transactionRepository.save(
                    new Transaction(null, type, amount, LocalDateTime.now(),  null, target.getAccountNumber(), dto.getDescription())
            );
        }
        else if (String.valueOf(Transaction.Type.WITHDRAWAL).equalsIgnoreCase(dto.getType())) {
            Transaction.Type type = Transaction.Type.valueOf(dto.getType());
            source = productRepository.findByAccountNumber(dto.getSourceProductId())
                    .orElseThrow(() -> new NotFoundException("Cuenta origen no existe"));

            if (source.getStatus() != Product.Status.ACTIVE) {
                throw new IllegalArgumentException("No se puede hacer retiro desde una cuenta inactiva o cancelada");
            }
            if (source.getBalance().compareTo(amount) < 0)
                throw new IllegalArgumentException("Saldo insuficiente para retiro");

            source.updateBalance(source.getBalance().subtract(amount));
            productRepository.save(source);

            return transactionRepository.save(
                    new Transaction(null, type, amount, LocalDateTime.now(),source.getAccountNumber(), null, dto.getDescription())
            );
        }
        else if  (String.valueOf(Transaction.Type.TRANSFER).equalsIgnoreCase(dto.getType())) {
            Transaction.Type type = Transaction.Type.valueOf(dto.getType());
            source = productRepository.findByAccountNumber(dto.getSourceProductId())
                    .orElseThrow(() -> new NotFoundException("Cuenta origen no existe"));
            target = productRepository.findByAccountNumber(dto.getTargetProductId())
                    .orElseThrow(() -> new NotFoundException("Cuenta destino no existe"));

            if (source.getStatus() != Product.Status.ACTIVE) {
                throw new IllegalArgumentException("No se puede transferir desde una cuenta inactiva o cancelada");
            }
            if (target.getStatus() != Product.Status.ACTIVE) {
                throw new IllegalArgumentException("No se puede transferir a una cuenta inactiva o cancelada");
            }

            if (source.getBalance().compareTo(amount) < 0)
                throw new IllegalArgumentException("Saldo insuficiente para transferir");


            //Aca paso el nuevo balance
            source.updateBalance(source.getBalance().subtract(amount));
            target.updateBalance(target.getBalance().add(amount));

            productRepository.save(source);
            productRepository.save(target);

            // Genera dos movimientos: débito y crédito
            transactionRepository.save(
                    new Transaction(null, type, amount.negate(), LocalDateTime.now(),  source.getAccountNumber(), target.getAccountNumber(), "Débito transferencia: " + dto.getDescription())
            );
            return transactionRepository.save(
                    new Transaction(null, type, amount, LocalDateTime.now(),  source.getAccountNumber(), target.getAccountNumber(), "Crédito transferencia: " + dto.getDescription())
            );
        } else {

            throw new IllegalArgumentException("Tipo de transacción no soportado");
        }

    }

    @Override
    public Transaction findById(Long id) {
        Optional<Transaction> opt = transactionRepository.findById(id);
        if (opt.isEmpty()) throw new NotFoundException("Transacción no encontrada");
        else{
            return opt.get();
        }


    }



    @Override
    public List<Transaction> findByProductAccountNumber(String productAccountNumber) {
        if(productRepository.findByAccountNumber(productAccountNumber).isEmpty()){
            throw new NotFoundException("Producto no encontrado");
        }else {
            return transactionRepository.findByProductAccountNumber(productAccountNumber);
        }


    }
    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }


}
