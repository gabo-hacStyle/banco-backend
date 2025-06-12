package gabs.infrastructure.repository.mapper;

import gabs.domain.entity.Transaction;
import gabs.infrastructure.repository.entity.ProductEntity;
import gabs.infrastructure.repository.entity.TransactionEntity;

public class TransactionMapper {

    public static TransactionEntity toEntity(Transaction tx) {
        if (tx == null) return null;
        TransactionEntity entity = new TransactionEntity();
        entity.setId(tx.getId());
        entity.setType(Transaction.Type.valueOf(tx.getType().name()));

        entity.setAmount(tx.getAmount());
        entity.setDate(tx.getDate());
        entity.setSourceAccountNumber(tx.getSourceProductId());
        entity.setTargetAccountNumber(tx.getTargetProductId());
        entity.setDescription(tx.getDescription());

        System.out.println("Mapped TransactionEntity: " + entity);
        return entity;
    }

    public static Transaction toDomain(TransactionEntity entity) {
        if (entity == null) return null;
        return new Transaction(
                entity.getId(),
                Transaction.Type.valueOf(entity.getType().name()),
                entity.getAmount(),
                entity.getDate(),

                entity.getSourceAccountNumber(),
                entity.getTargetAccountNumber(),
                entity.getDescription()
        );
    }
}