package gabs.application.service;

import gabs.application.dto.ProductCreateDTO;
import gabs.domain.entity.Client;
import gabs.domain.entity.Product;
import gabs.domain.exceptions.NotFoundException;
import gabs.domain.ports.ClientRepository;
import gabs.domain.ports.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    ProductRepository repo;

    @Mock
    ClientRepository repoClient;

    @InjectMocks
    ProductService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowIfTypeInvalid() {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setType("OTRO");
        dto.setExemptGMF(false);
        dto.setClientId(1L);
        when(repoClient.findById(1L)).thenReturn(Optional.of(mock(Client.class)));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createProduct(dto));
        assertEquals("Tipo de producto inválido", ex.getMessage());

    }

    @Test
    void shouldThrowIfClientDoesNotExist() {
        // Arrange
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setType("SAVINGS");
        dto.setExemptGMF(false);
        dto.setClientId(99L); // Cliente que no existe

        when(repoClient.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception ex = assertThrows(NotFoundException.class, () -> service.createProduct(dto));
        assertTrue(ex.getMessage().contains("Cliente no existe"));
        verify(repo, never()).save(any());
    }

    @Test
    void shouldGenerateUniqueAccountNumberSavings() {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setType("SAVINGS");
        dto.setExemptGMF(false);
        dto.setClientId(1L);

        when(repoClient.findById(1L)).thenReturn(Optional.of(mock(Client.class)));
        // Simula que el primer número ya existe y el segundo no
        when(repo.existsByAccountNumber(anyString()))
                .thenReturn(true) // primer intento
                .thenReturn(false); // segundo intento

        Product expectedProduct = mock(Product.class);
        when(repo.save(any())).thenReturn(expectedProduct);

        Product result = service.createProduct(dto);

        verify(repo, times(2)).existsByAccountNumber(anyString());
        verify(repo).save(any());
        assertEquals(expectedProduct, result);
    }

    @Test
    void shouldGenerateUniqueAccountNumberChecking() {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setType("CHECKING");
        dto.setExemptGMF(false);
        dto.setClientId(1L);

        when(repoClient.findById(1L)).thenReturn(Optional.of(mock(Client.class)));
        // Simula que el primer número ya existe y el segundo no
        when(repo.existsByAccountNumber(anyString()))
                .thenReturn(true) // primer intento
                .thenReturn(true) // segundo intento
                .thenReturn(false);
        Product expectedProduct = mock(Product.class);
        when(repo.save(any())).thenReturn(expectedProduct);

        Product result = service.createProduct(dto);

        verify(repo, times(3)).existsByAccountNumber(anyString());
        verify(repo).save(any());
        assertEquals(expectedProduct, result);
    }

    @Test
    void shouldNotAllowMultipleExemptGMFAccountsPerClient() {
        Long clientId = 1L;
        // Simula existente exenta de GMF
        Product existing = mock(Product.class);
        when(existing.isExemptGMF()).thenReturn(true);
        when(repoClient.findById(clientId)).thenReturn(Optional.of(mock(Client.class)));
        when(repo.findByClientId(clientId)).thenReturn(List.of(existing));

        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setClientId(clientId);
        dto.setExemptGMF(true);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> service.createProduct(dto));
        assertEquals("El cliente ya tiene una cuenta exenta de GMF", ex.getMessage());
    }

    @Test
    void shouldThrowClientNotFoundWhenFindByClientIdIsEmpty() {
        when(repoClient.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class,
                () -> service.findByClientId(99L));
        assertEquals("Cliente no existe", ex.getMessage());
    }

    @Test
    void shouldThrowIfProductDoesntExistWhenActivate(){
        when(repo.findByAccountNumber("5301000000")).thenReturn(Optional.empty());
        Exception ex = assertThrows(NotFoundException.class, () -> service.activateProduct("5301000000"));
        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

    @Test
    void shouldThrowIfProductDoesntExistWhenInactivate(){
        when(repo.findByAccountNumber("5301000000")).thenReturn(Optional.empty());
        Exception ex = assertThrows(NotFoundException.class, () -> service.inactivateProduct("5301000000"));
        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

    @Test
    void shouldThrowIfProductDoesntExistWhenCancelling(){
        when(repo.findByAccountNumber("5301000000")).thenReturn(Optional.empty());
        Exception ex = assertThrows(NotFoundException.class, () -> service.cancelProduct("5301000000"));
        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

}