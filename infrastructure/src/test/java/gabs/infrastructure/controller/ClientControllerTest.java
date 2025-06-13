package gabs.infrastructure.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import gabs.application.dto.ClientDTO;
import gabs.application.dto.CreateClientDTO;
import gabs.application.ports.ClientUseCase;

import gabs.application.ports.ProductUseCases;
import gabs.application.service.ProductService;
import gabs.infrastructure.repository.adapter.JpaProductRepository;
import gabs.infrastructure.repository.springdata.SpringDataProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ClientController.class,
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ClientController.class)
)
@ContextConfiguration(classes = {ClientController.class}) // SOLO tu controller
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientUseCase clientUseCase;


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
        Mockito.when(clientUseCase.createClient(any(CreateClientDTO.class))).thenReturn(clientDTO);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createClientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientDTO.getId()))
                .andExpect(jsonPath("$.firstName").value(clientDTO.getFirstName()));
    }


    @Test
    void shouldFindClients() throws Exception {

        Mockito.when(clientUseCase.getAllClients()).thenReturn(List.of(clientDTO));

        mockMvc.perform(get("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(clientDTO.getId()))
                .andExpect(jsonPath("$[0].firstName").value(clientDTO.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(clientDTO.getLastName()))
                .andExpect(jsonPath("$[0].email").value(clientDTO.getEmail()));

    }



}