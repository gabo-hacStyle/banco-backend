package gabs.application.service;


import gabs.application.ports.ProductUseCases;
import gabs.domain.entity.Product;
import gabs.domain.entity.ProductNumberGenerator;
import gabs.domain.ports.ProductRepository;
import org.springframework.stereotype.Service;

import gabs.application.dto.ProductCreateDTO;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements ProductUseCases {

    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(ProductCreateDTO dto) {
        String accountNumber;
        Product product;
        if ("SAVINGS".equalsIgnoreCase(dto.getType())) {

            product = Product.createSavings(dto.getClientId(), dto.isExemptGMF());
        } else if ("CHECKING".equalsIgnoreCase(dto.getType())) {

            product = Product.createChecking(dto.getClientId(), dto.isExemptGMF());
        } else {
            throw new IllegalArgumentException("Tipo de producto inválido");
        }
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findByAccountNumber(String accountNumber) {
        return productRepository.findByAccountNumber(accountNumber);
    }
    @Override
    public List<Product> findByClientId(Long clientId) {
        return productRepository.findByClientId(clientId);
    }
}
