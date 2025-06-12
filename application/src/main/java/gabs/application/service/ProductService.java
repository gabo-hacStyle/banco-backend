package gabs.application.service;


import gabs.application.ports.ProductUseCases;
import gabs.domain.entity.Product;
import gabs.domain.entity.ProductNumberGenerator;
import gabs.domain.ports.ClientRepository;
import gabs.domain.ports.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import gabs.application.dto.ProductCreateDTO;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class ProductService implements ProductUseCases {

    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    @Override
    public Product createProduct(ProductCreateDTO dto) {
        if (clientRepository.findById(dto.getClientId()).isEmpty()) {
            throw new IllegalArgumentException("Cliente no existe");
        }

        if (dto.isExemptGMF()) {
            boolean alreadyExempt = productRepository.findByClientId(dto.getClientId())
                    .stream().anyMatch(Product::isExemptGMF);
            if (alreadyExempt) {
                throw new IllegalArgumentException("El cliente ya tiene una cuenta exenta de GMF");
            }
        }

        //Here we automatically generate a number
        String accountNumber;
        int maxAttempts = 10;
        int attempts = 0;
        do {
            if ("SAVINGS".equalsIgnoreCase(dto.getType())) {
                accountNumber = ProductNumberGenerator.generateSavingsAccountNumber();
            } else if ("CHECKING".equalsIgnoreCase(dto.getType())) {
                accountNumber = ProductNumberGenerator.generateCheckingAccountNumber();
            } else {
                throw new IllegalArgumentException("Tipo de producto inválido");
            }
            attempts++;
            if (attempts > maxAttempts) {
                throw new IllegalStateException("No se pudo generar un número de cuenta único tras varios intentos");
            }
        } while (productRepository.existsByAccountNumber(accountNumber));


        Product product;
        if (String.valueOf(Product.Type.SAVINGS).equalsIgnoreCase(dto.getType())) {

            product = Product.createSavings(dto.getClientId(), dto.isExemptGMF(), accountNumber);
        } else if (String.valueOf(Product.Type.CHECKING).equalsIgnoreCase(dto.getType())) {

            product = Product.createChecking(dto.getClientId(), dto.isExemptGMF(), accountNumber);
        } else {
            throw new IllegalArgumentException("Tipo de producto inválido");
        }
        return productRepository.save(product);
    }

    @Override
    public Product activateProduct(String accountNumber) {

        Optional<Product> product = productRepository.findByAccountNumber(accountNumber);
        if (product.isEmpty()) throw new IllegalArgumentException("Producto no encontrado");

        Product prod = product.get();
        prod.activate();
        productRepository.save(prod);

        return prod;
    }

    @Override
    public Product inactivateProduct(String accountNumber) {
        Optional<Product> product = productRepository.findByAccountNumber(accountNumber);
        if (product.isEmpty()) throw new IllegalArgumentException("Producto no encontrado");

        Product prod = product.get();
        prod.inactivate();
        productRepository.save(prod);

        return prod;
    }

    @Override
    public Product cancelProduct(String accountNumber) {
        Optional<Product> product = productRepository.findByAccountNumber(accountNumber);
        if (product.isEmpty()) throw new IllegalArgumentException("Producto no encontrado");
        Product prod = product.get();
        prod.cancel();
        productRepository.save(prod);

        return prod;
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
