package gabs.infrastructure.controller;

import gabs.application.dto.ClientDTO;
import gabs.application.ports.ClientUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientUseCase clientUseCase;

    @Test
    public void shouldReturn201WhenClientCreated() throws Exception {
        ClientDTO dto = new ClientDTO();
        dto.firstName = "Juan";
        dto.lastName = "Perez";
        dto.birthDate = LocalDate.now().minusYears(20);
        dto.email = "juan@mail.com";

        when(clientUseCase.createClient(any())).thenReturn(dto);

        mockMvc.perform(post("/clients")
                        .contentType("application/json")
                        .content("{\"identificationType\":\"CC\",\"identificationNumber\":\"123\",\"firstName\":\"Juan\",\"lastName\":\"Perez\",\"email\":\"juan@mail.com\",\"birthDate\":\"2000-01-01\"}"))
                .andExpect(status().isOk());
    }

}