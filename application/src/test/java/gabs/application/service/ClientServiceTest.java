package gabs.application.service;

import gabs.application.dto.ClientDTO;
import gabs.application.dto.CreateClientDTO;
import gabs.domain.entity.Client;
import gabs.domain.ports.ClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClientServiceTest {
    private ClientRepository repository;
    private ClientService service;

    @BeforeEach
    void setUp() {
        repository = mock(ClientRepository.class);
       service = new ClientService(repository);
    }

    @AfterEach
    void tearDown() {
    }



    @Test
    void createClientShouldThrowIfExists() {
        when(repository.existsByIdentificationNumber("1234567")).thenReturn(true);
        CreateClientDTO dto = new CreateClientDTO("CC", "1234567", "Ana", "Lopez", "ana@mail.com", LocalDate.now().minusYears(20));
        assertThrows(IllegalArgumentException.class, () -> service.createClient(dto));
    }

    @Test
    void deleteClientShouldThrowIfLinkedProducts() {
        when(repository.hasLinkedProducts(1L)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.deleteClient(1L));
    }

    @Test
    void createClientShouldReturnExceptionCauseUnderAge(){
        CreateClientDTO dto = new CreateClientDTO( "CC", "1234567", "Ana", "Lopez", "ana@mail.com", LocalDate.now().minusYears(10));


        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
            service.createClient(dto)
        );

        Assertions.assertEquals("El cliente debe ser mayor de edad", exception.getMessage());
    }




    @Test
    void updateClientShouldReturnExceptionBadEmail(){
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(new Client(1L, "CC", "1234567", "Ana", "Lopez",
                        "ana@mail.com", LocalDate.now().minusYears(20), LocalDateTime.now(), null)));

        CreateClientDTO updatedValues = new CreateClientDTO("CC", "1234567", "Ana", "Lopez", "anamail.com", LocalDate.now().minusYears(20));





        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                service.updateClient(1L, updatedValues)
        );

        Assertions.assertEquals("El email no tiene un formato válido", exception.getMessage());
    }

   // @Test
   // void createClient() {
   // }
//
   // @Test
   // void updateClient() {
   // }
//
   // @Test
   // void deleteClient() {
   // }
//
   // @Test
   // void getAllClients() {
   // }
//
   // @Test
   // void getClientById() {
   // }
}