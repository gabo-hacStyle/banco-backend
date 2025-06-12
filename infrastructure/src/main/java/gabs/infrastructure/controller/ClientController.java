package gabs.infrastructure.controller;


import gabs.application.dto.ClientDTO;
import gabs.application.dto.CreateClientDTO;
import gabs.application.ports.ClientUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")

public class ClientController {
    private final ClientUseCase clientUseCase;

    @Autowired
    public ClientController(ClientUseCase clientUseCase) {
        this.clientUseCase = clientUseCase;
    }

    @PostMapping
    public ClientDTO create(@RequestBody CreateClientDTO clientDTO) {
        return clientUseCase.createClient(clientDTO);
    }

    @PutMapping("/{id}")
    public ClientDTO update(@PathVariable Long id, @RequestBody CreateClientDTO clientDTO) {
        return clientUseCase.updateClient(id, clientDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clientUseCase.deleteClient(id);

    }

    @GetMapping
    public List<ClientDTO> getAll() {
        return clientUseCase.getAllClients();
    }

    @GetMapping("/{id}")
    public ClientDTO getById(@PathVariable Long id) {
        return clientUseCase.getClientById(id);
    }
}
