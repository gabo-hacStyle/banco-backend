package gabs.infrastructure.repository.mapper;

import gabs.domain.entity.Transaction;
import gabs.infrastructure.repository.entity.ProductEntity;
import gabs.infrastructure.repository.entity.TransactionEntity;

public class TransactionMapper {

    public static TransactionEntity toEntity(Transaction tx, ProductEntity source, ProductEntity target) {
        if (tx == null) return null;
        TransactionEntity entity = new TransactionEntity();
        entity.setId(tx.getId());
        entity.setType(Transaction.Type.valueOf(tx.getType().name()));
        entity.setStatus(Transaction.Status.valueOf(tx.getStatus().name()));
        entity.setAmount(tx.getAmount());
        entity.setDate(tx.getDate());
        entity.setSourceProduct(source);
        entity.setTargetProduct(target);
        entity.setDescription(tx.getDescription());
        return entity;
    }
    public static Transaction toDomain(TransactionEntity entity) {
        if (entity == null) return null;
        return new Transaction(
                entity.getId(),
                Transaction.Type.valueOf(entity.getType().name()),
                entity.getAmount(),
                entity.getDate(),
                Transaction.Status.valueOf(entity.getStatus().name()),
                entity.getSourceProduct() != null ? entity.getSourceProduct().getId() : null,
                entity.getTargetProduct() != null ? entity.getTargetProduct().getId() : null,
                entity.getDescription()
        );
    }
}
