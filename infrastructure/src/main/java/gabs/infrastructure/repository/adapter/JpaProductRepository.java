package gabs.infrastructure.repository.adapter;


import gabs.domain.entity.Product;
import gabs.domain.ports.ProductRepository;
import gabs.infrastructure.repository.entity.ClientEntity;
import gabs.infrastructure.repository.entity.ProductEntity;
import gabs.infrastructure.repository.mapper.ProductMapper;
import gabs.infrastructure.repository.springdata.SpringDataClientRepository;
import gabs.infrastructure.repository.springdata.SpringDataProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository

public class JpaProductRepository implements ProductRepository {
    private final SpringDataProductRepository productRepo;
    private final SpringDataClientRepository clientRepo;

    public JpaProductRepository(SpringDataProductRepository productRepo, SpringDataClientRepository clientRepo) {
        this.productRepo = productRepo;
        this.clientRepo = clientRepo;
    }

    @Override
    public Product save(Product product) {
        ClientEntity clientEntity = clientRepo.findById(product.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        ProductEntity entity = ProductMapper.toEntity(product, clientEntity);
        ProductEntity saved = productRepo.save(entity);
        return ProductMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepo.findById(id).map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findByClientId(Long clientId) {
        return productRepo.findByClient_Id(clientId).stream().map(ProductMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findByAccountNumber(String accountNumber) {
        return productRepo.findByAccountNumber(accountNumber).map(ProductMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        productRepo.deleteById(id);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return productRepo.existsByAccountNumber(accountNumber);
    }
}
