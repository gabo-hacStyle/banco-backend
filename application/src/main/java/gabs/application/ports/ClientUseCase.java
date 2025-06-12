package gabs.application.ports;

import gabs.application.dto.ClientDTO;
import gabs.application.dto.CreateClientDTO;

import java.util.List;

public interface ClientUseCase {
    ClientDTO createClient(CreateClientDTO clientDTO);
    ClientDTO updateClient(Long id, CreateClientDTO clientDTO);
    void deleteClient(Long id);
    List<ClientDTO> getAllClients();
    ClientDTO getClientById(Long id);

}
