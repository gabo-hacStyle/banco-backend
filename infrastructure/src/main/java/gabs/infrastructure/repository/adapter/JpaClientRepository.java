package gabs.infrastructure.repository.adapter;

import gabs.domain.entity.Client;
import gabs.domain.ports.ClientRepository;
import gabs.infrastructure.repository.springdata.SpringDataClientRepository;
import gabs.infrastructure.repository.entity.ClientEntity;
import gabs.infrastructure.repository.mapper.ClientMapper;
import gabs.infrastructure.repository.springdata.SpringDataProductRepository;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaClientRepository implements ClientRepository {

    private final SpringDataClientRepository springRepo;
    private final SpringDataProductRepository productRepository;

    @Autowired
    public JpaClientRepository(SpringDataClientRepository springRepo, SpringDataProductRepository productRepository) {
        this.springRepo = springRepo;
        this.productRepository = productRepository;
    }

    @Override
    public Client save(Client client) {
        ClientEntity entity = ClientMapper.toEntity(client);
        ClientEntity saved = springRepo.save(entity);
        return ClientMapper.toDomain(saved);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return springRepo.findById(id).map(ClientMapper::toDomain);
    }

    @Override
    public List<Client> findAll() {
        return springRepo.findAll().stream().map(ClientMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        springRepo.deleteById(id);
    }

    @Override
    public boolean existsByIdentificationNumber(String identificationNumber) {
        return springRepo.existsByIdentificationNumber(identificationNumber);
    }

    @Override
    public boolean hasLinkedProducts(Long clientId) {
        return productRepository.existsByClientId(clientId);
    }
}