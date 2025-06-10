package gabs.infrastructure.repository.adapter;




import gabs.domain.entity.Transaction;
import gabs.domain.ports.TransactionRepository;
import gabs.infrastructure.repository.entity.ProductEntity;
import gabs.infrastructure.repository.entity.TransactionEntity;
import gabs.infrastructure.repository.mapper.TransactionMapper;
import gabs.infrastructure.repository.springdata.SpringDataTransactionRepository;
import gabs.infrastructure.repository.springdata.SpringDataProductRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaTransactionRepository implements TransactionRepository {

    private final SpringDataTransactionRepository txRepo;
    private final SpringDataProductRepository productRepo;

    public JpaTransactionRepository(SpringDataTransactionRepository txRepo, SpringDataProductRepository productRepo) {
        this.txRepo = txRepo;
        this.productRepo = productRepo;
    }

    @Override
    public Transaction save(Transaction transaction) {
        ProductEntity source = transaction.getSourceProductId() != null ?
                productRepo.findById(transaction.getSourceProductId()).orElse(null) : null;
        ProductEntity target = transaction.getTargetProductId() != null ?
                productRepo.findById(transaction.getTargetProductId()).orElse(null) : null;

        TransactionEntity entity = TransactionMapper.toEntity(transaction, source, target);
        TransactionEntity saved = txRepo.save(entity);
        return TransactionMapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return txRepo.findById(id).map(TransactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findByProductId(Long productId) {
        return txRepo.findBySourceProduct_IdOrTargetProduct_Id(productId, productId)
                .stream().map(TransactionMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findAll() {
        return txRepo.findAll().stream().map(TransactionMapper::toDomain).collect(Collectors.toList());
    }
}
