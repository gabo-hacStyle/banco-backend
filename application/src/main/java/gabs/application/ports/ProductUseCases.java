package gabs.application.ports;

import gabs.application.dto.ProductCreateDTO;
import gabs.domain.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductUseCases {

    Product createProduct(ProductCreateDTO dto);
    Product activateProduct(String accountNumber);
    Product inactivateProduct(String accountNumber);
    Product cancelProduct(String accountNumber);

    Product findByAccountNumber(String accountNumber);
    List<Product> findByClientId(Long clientId);
}
