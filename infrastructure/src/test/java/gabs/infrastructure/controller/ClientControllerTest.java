package gabs.infrastructure.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import gabs.application.dto.ClientDTO;
import gabs.application.dto.CreateClientDTO;
import gabs.application.ports.ClientUseCase;


import gabs.application.service.ClientService;
import gabs.domain.exceptions.NotFoundException;
import gabs.domain.ports.ClientRepository;
import gabs.infrastructure.controller.errors.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ClientController.class,
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ClientController.class)
)
@ContextConfiguration(classes = {ClientController.class, GlobalExceptionHandler.class})
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientUseCase clientUseCase;
    @MockitoBean
    private ClientRepository clientRepository;


    @Autowired
    private ObjectMapper objectMapper;


    private ClientDTO clientDTO;
    private CreateClientDTO createClientDTO;


    @BeforeEach
    void setUp() {
         createClientDTO = new CreateClientDTO();
        createClientDTO.setFirstName("Gabo");
        createClientDTO.setLastName("HacStyle");
        createClientDTO.setEmail("gabo@email.com");

        clientDTO = new ClientDTO();
        clientDTO.setId(1L);
        clientDTO.setFirstName("Gabo");
        clientDTO.setLastName("HacStyle");
        clientDTO.setEmail("gabo@email.com");
    }

    @Test
    void createClient_returnsCreatedClient() throws Exception {
        when(clientUseCase.createClient(any(CreateClientDTO.class))).thenReturn(clientDTO);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientDTO.getId()))
                .andExpect(jsonPath("$.firstName").value(clientDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(clientDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(clientDTO.getEmail()));
    }

    @Test
    void createClient_shouldReturnBadRequest_whenValidationFails() throws Exception {
        createClientDTO.setFirstName(""); // Inválido

        when(clientUseCase.createClient(any(CreateClientDTO.class)))
                .thenThrow(new IllegalArgumentException("El nombre debe tener al menos 2 caracteres"));

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("El nombre debe tener al menos 2 caracteres"));
    }

    @Test
    void updateClient_returnsUpdatedClient() throws Exception {
        when(clientUseCase.updateClient(eq(1L), any(CreateClientDTO.class))).thenReturn(clientDTO);

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientDTO.getId()))
                .andExpect(jsonPath("$.firstName").value(clientDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(clientDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(clientDTO.getEmail()));
    }

    @Test
    void updateClient_shouldReturnNotFound_whenClientDoesNotExist() throws Exception {


        doThrow(new NotFoundException("Cliente no encontrado")).when(clientUseCase).updateClient(99L, createClientDTO);

        mockMvc.perform(put("/api/clients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteClient_returnsOk() throws Exception {
        doNothing().when(clientUseCase).deleteClient(1L);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteClient_shouldReturnNotFound_whenClientDoesNotExist() throws Exception {
        doThrow(new NotFoundException("Cliente no encontrado")).when(clientUseCase).deleteClient(99L);

        mockMvc.perform(delete("/api/clients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllClients_returnsListOfClients() throws Exception {
        when(clientUseCase.getAllClients()).thenReturn(List.of(clientDTO));

        mockMvc.perform(get("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(clientDTO.getId()))
                .andExpect(jsonPath("$[0].firstName").value(clientDTO.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(clientDTO.getLastName()))
                .andExpect(jsonPath("$[0].email").value(clientDTO.getEmail()));
    }

    @Test
    void getById_returnsClient() throws Exception {
        when(clientUseCase.getClientById(1L)).thenReturn(clientDTO);

        mockMvc.perform(get("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientDTO.getId()))
                .andExpect(jsonPath("$.firstName").value(clientDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(clientDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(clientDTO.getEmail()));
    }

    @Test
    void getById_shouldReturnNotFound_whenClientDoesNotExist() throws Exception {
        when(clientUseCase.getClientById(99L)).thenThrow(new NotFoundException("Cliente no encontrado"));

        mockMvc.perform(get("/api/clients/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }





}