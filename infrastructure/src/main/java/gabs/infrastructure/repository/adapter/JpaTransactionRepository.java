package gabs.infrastructure.repository.adapter;




import gabs.domain.entity.Transaction;
import gabs.domain.ports.TransactionRepository;

import gabs.infrastructure.repository.entity.TransactionEntity;
import gabs.infrastructure.repository.mapper.TransactionMapper;
import gabs.infrastructure.repository.springdata.SpringDataTransactionRepository;

import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaTransactionRepository implements TransactionRepository {

    private final SpringDataTransactionRepository txRepo;


    public JpaTransactionRepository(SpringDataTransactionRepository txRepo) {
        this.txRepo = txRepo;

    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = TransactionMapper.toEntity(transaction);

        System.out.println("Saving TransactionEntity: " + entity);
        TransactionEntity saved = txRepo.save(entity);
        System.out.println("Saved TransactionEntity: " + saved);
        return TransactionMapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return txRepo.findById(id).map(TransactionMapper::toDomain);
    }


    @Override
    public List<Transaction> findByProductAccountNumber(String productAccountNumber) {
        return txRepo.findBySourceAccountNumberOrTargetAccountNumber(productAccountNumber, productAccountNumber)
                .stream().map(TransactionMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findAll() {
        return txRepo.findAll().stream().map(TransactionMapper::toDomain).collect(Collectors.toList());
    }
}
