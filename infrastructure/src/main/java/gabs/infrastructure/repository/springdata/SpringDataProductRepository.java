package gabs.infrastructure.repository.springdata;

import gabs.infrastructure.repository.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByAccountNumber(String accountNumber);
    List<ProductEntity> findByClient_Id(Long clientId);
    boolean existsByAccountNumber(String accountNumber);
    boolean existsByClientId(Long clientId);
}
