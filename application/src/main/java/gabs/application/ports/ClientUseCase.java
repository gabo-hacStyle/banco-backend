package gabs.application.ports;

import gabs.application.dto.ClientDTO;

import java.util.List;

public interface ClientUseCase {
    ClientDTO createClient(ClientDTO clientDTO);
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    void deleteClient(Long id);
    List<ClientDTO> getAllClients();
    ClientDTO getClientById(Long id);

}
