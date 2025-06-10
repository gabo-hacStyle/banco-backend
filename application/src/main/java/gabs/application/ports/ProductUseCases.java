package gabs.application.ports;

import gabs.application.dto.ProductCreateDTO;
import gabs.domain.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductUseCases {

    Product createProduct(ProductCreateDTO dto);

    Optional<Product> findByAccountNumber(String accountNumber);
    List<Product> findByClientId(Long clientId);
}
