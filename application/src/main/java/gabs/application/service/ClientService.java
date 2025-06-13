package gabs.application.service;

import gabs.application.dto.ClientDTO;
import gabs.application.dto.CreateClientDTO;
import gabs.application.ports.ClientUseCase;
import gabs.domain.entity.Client;

import gabs.domain.exceptions.NotFoundException;
import gabs.domain.ports.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService  implements ClientUseCase {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDTO createClient(CreateClientDTO dto) {

        if (clientRepository.existsByIdentificationNumber(dto.identificationNumber)) {
            throw new IllegalArgumentException("Ya existe un cliente con ese número de identificación");
        }
        Client client = new Client(null, dto.identificationType, dto.identificationNumber, dto.firstName, dto.lastName, dto.email, dto.birthDate, LocalDateTime.now(), null);
        Client saved = clientRepository.save(client);
        return toDTO(saved);
    }

    @Override
    public ClientDTO updateClient(Long id, CreateClientDTO dto) {
        Optional<Client> opt = clientRepository.findById(id);
        if (opt.isEmpty()) throw new NotFoundException("Cliente no encontrado");

        Client client = opt.get();
        if (dto.identificationType != null) client.setIdentificationType(dto.identificationType);
        if (dto.identificationNumber != null) client.setIdentificationNumber(dto.identificationNumber);
        if (dto.firstName != null) client.setFirstName(dto.firstName);
        if (dto.lastName != null) client.setLastName(dto.lastName);
        if (dto.email != null) client.setEmail(dto.email);
        if (dto.birthDate != null) client.setBirthDate(dto.birthDate);
        client.setUpdateDate(LocalDateTime.now());

        Client updated = clientRepository.save(client);
        return toDTO(updated);
    }

    @Override
    public void deleteClient(Long id) {
        ClientDTO client = this.getClientById(id);


        if (clientRepository.hasLinkedProducts(id)) {
            throw new IllegalArgumentException("No se puede eliminar el cliente porque tiene productos vinculados");
        }
        if(clientRepository.findById(id).isPresent()) {
            clientRepository.deleteById(id);
        }
        else {
            throw new NotFoundException("Cliente no encontrado");
        }

    }

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ClientDTO getClientById(Long id) {
        return clientRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

    }

    private ClientDTO toDTO(Client client) {
        return new ClientDTO(
                client.getId(), client.getIdentificationType(), client.getIdentificationNumber(),
                client.getFirstName(), client.getLastName(), client.getEmail(), client.getBirthDate(),
                client.getCreationDate(), client.getUpdateDate());
    }
}
