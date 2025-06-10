package gabs.domain.ports;

import gabs.domain.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(Long id);
    List<Client> findAll();
    void deleteById(Long id);
    boolean existsByIdentificationNumber(String identificationNumber);
    boolean hasLinkedProducts(Long clientId); // Para verificar si puede eliminarse

}
