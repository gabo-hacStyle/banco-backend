package gabs.domain.ports;

import gabs.domain.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findByClientId(Long clientId);
    Optional<Product> findByAccountNumber(String accountNumber);
    void deleteById(Long id);
    boolean existsByAccountNumber(String accountNumber);
}