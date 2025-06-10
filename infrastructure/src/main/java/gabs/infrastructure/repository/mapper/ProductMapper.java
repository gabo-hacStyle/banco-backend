package gabs.infrastructure.repository.mapper;

import gabs.domain.entity.Product;
import gabs.infrastructure.repository.entity.ClientEntity;
import gabs.infrastructure.repository.entity.ProductEntity;

public class ProductMapper {
    public static ProductEntity toEntity(Product product, ClientEntity clientEntity) {
        if (product == null) return null;
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setType(Product.Type.valueOf(product.getType().name()));
        entity.setAccountNumber(product.getAccountNumber());
        entity.setStatus(Product.Status.valueOf(product.getStatus().name()));
        entity.setBalance(product.getBalance());
        entity.setExemptGMF(product.isExemptGMF());
        entity.setCreationDate(product.getCreationDate());
        entity.setUpdateDate(product.getUpdateDate());
        entity.setClient(clientEntity);
        return entity;
    }
    public static Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        return new Product(
                entity.getId(),
                Product.Type.valueOf(entity.getType().name()),
                entity.getAccountNumber(),
                Product.Status.valueOf(entity.getStatus().name()),
                entity.getBalance(),
                entity.isExemptGMF(),
                entity.getCreationDate(),
                entity.getUpdateDate(),
                entity.getClient().getId()
        );
    }

}
