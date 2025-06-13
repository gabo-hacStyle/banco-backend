package gabs.application.service;

import gabs.application.dto.TransactionCreateDTO;
import gabs.domain.entity.Product;
import gabs.domain.entity.Transaction;
import gabs.domain.exceptions.NotFoundException;
import gabs.domain.ports.ProductRepository;
import gabs.domain.ports.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    private TransactionRepository transactionRepository;
    private ProductRepository productRepository;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        productRepository = mock(ProductRepository.class);
        service = new TransactionService(transactionRepository, productRepository);
    }

    @Test
    void shouldThrowIfTypeInvalid() {
        TransactionCreateDTO dto = createDto("OTRO", "1", null, "1000.00");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
        assertEquals("Tipo de transacción no soportado", ex.getMessage());
    }

    @Test
    void createsDeposit_andUpdatesBalance() {
        Product target = mock(Product.class);

        when(productRepository.findByAccountNumber("2L")).thenReturn(Optional.of(target));
        when(target.getBalance()).thenReturn(new BigDecimal("1000.00"));
        when(target.getStatus()).thenReturn(Product.Status.ACTIVE);
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransactionCreateDTO dto = createDto("DEPOSIT", null, "2L", "1000.00");
        Transaction tx = service.createTransaction(dto);

        verify(target).updateBalance(new BigDecimal("2000.00"));
        verify(productRepository).save(target);
        verify(transactionRepository).save(any(Transaction.class));
        assertEquals(Transaction.Type.DEPOSIT, tx.getType());
        assertEquals(new BigDecimal("1000.00"), tx.getAmount());
        assertNull(tx.getSourceProductId());
    }

    @Test
    void createsWithdrawal_andUpdatesBalance() {
        Product source = mock(Product.class);
        when(productRepository.findByAccountNumber("10L")).thenReturn(Optional.of(source));
        when(source.getBalance()).thenReturn(new BigDecimal("300.00"));
        when(source.getStatus()).thenReturn(Product.Status.ACTIVE);
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransactionCreateDTO dto = createDto("WITHDRAWAL", "10L", null, "100.00");
        Transaction tx = service.createTransaction(dto);

        verify(source).updateBalance(new BigDecimal("200.00"));
        verify(productRepository).save(source);
        verify(transactionRepository).save(any(Transaction.class));
        assertEquals(Transaction.Type.WITHDRAWAL, tx.getType());
        assertEquals(new BigDecimal("100.00"), tx.getAmount());
        assertNull(tx.getTargetProductId());
    }

    @Test
    void createsTransfer_andUpdatesBalance() {
        Product source = mock(Product.class);
        Product target = mock(Product.class);
        when(productRepository.findByAccountNumber("1L")).thenReturn(Optional.of(source));
        when(productRepository.findByAccountNumber("2L")).thenReturn(Optional.of(target));
        when(source.getBalance()).thenReturn(new BigDecimal("400.00"));
        when(target.getBalance()).thenReturn(new BigDecimal("50.00"));
        when(target.getStatus()).thenReturn(Product.Status.ACTIVE);
        when(source.getStatus()).thenReturn(Product.Status.ACTIVE);
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransactionCreateDTO dto = createDto("TRANSFER", "1L", "2L", "150.00");
        Transaction tx = service.createTransaction(dto);

        verify(source).updateBalance(new BigDecimal("250.00"));
        verify(target).updateBalance(new BigDecimal("200.00"));
        verify(productRepository).save(source);
        verify(productRepository).save(target);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        assertEquals(Transaction.Type.TRANSFER, tx.getType());
        assertEquals(new BigDecimal("150.00"), tx.getAmount());
    }

    @ParameterizedTest
    @MethodSource("invalidAccountStatusProvider")
    void deposit_toInvalidAccountStatus_throws(Product.Status status) {
        Product target = mock(Product.class);
        when(productRepository.findByAccountNumber("2L")).thenReturn(Optional.of(target));
        when(target.getStatus()).thenReturn(status);

        TransactionCreateDTO dto = createDto("DEPOSIT", null, "2L", "100.00");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));

        assertEquals("No se puede hacer depósito a una cuenta inactiva o cancelada", ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidAccountStatusProvider")
    void withdrawal_fromInvalidAccountStatus_throws(Product.Status status) {
        Product source = mock(Product.class);
        when(productRepository.findByAccountNumber("1L")).thenReturn(Optional.of(source));
        when(source.getStatus()).thenReturn(status);

        TransactionCreateDTO dto = createDto("WITHDRAWAL", "1L", null, "50.00");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));

        assertEquals("No se puede hacer retiro desde una cuenta inactiva o cancelada", ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidAccountStatusProvider")
    void transfer_fromInvalidAccountStatus_throws(Product.Status status) {
        Product source = mock(Product.class);
        Product target = mock(Product.class);
        when(productRepository.findByAccountNumber("1L")).thenReturn(Optional.of(source));
        when(productRepository.findByAccountNumber("2L")).thenReturn(Optional.of(target));
        when(source.getStatus()).thenReturn(status);
        when(target.getStatus()).thenReturn(Product.Status.ACTIVE);

        TransactionCreateDTO dto = createDto("TRANSFER", "1L", "2L", "25.00");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
        assertEquals("No se puede transferir desde una cuenta inactiva o cancelada", ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidAccountStatusProvider")
    void transfer_toInvalidAccountStatus_throws(Product.Status status) {
        Product source = mock(Product.class);
        Product target = mock(Product.class);
        when(productRepository.findByAccountNumber("1L")).thenReturn(Optional.of(source));
        when(productRepository.findByAccountNumber("2L")).thenReturn(Optional.of(target));
        when(source.getStatus()).thenReturn(Product.Status.ACTIVE);
        when(target.getStatus()).thenReturn(status);

        TransactionCreateDTO dto = createDto("TRANSFER", "1L", "2L", "25.00");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));

        assertEquals("No se puede transferir a una cuenta inactiva o cancelada", ex.getMessage());
    }

    @Test
    void withdrawal_withInsufficientBalance_throws() {
        Product source = mock(Product.class);
        when(productRepository.findByAccountNumber("1L")).thenReturn(Optional.of(source));
        when(source.getBalance()).thenReturn(new BigDecimal("50.00"));

        TransactionCreateDTO dto = createDto("WITHDRAWAL", "1L", null, "200.00");
        assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
    }

    @Test
    void transfer_withInsufficientBalance_throws() {
        Product source = mock(Product.class);
        Product target = mock(Product.class);
        when(productRepository.findByAccountNumber("1L")).thenReturn(Optional.of(source));
        when(productRepository.findByAccountNumber("2L")).thenReturn(Optional.of(target));
        when(source.getBalance()).thenReturn(new BigDecimal("100.00"));

        TransactionCreateDTO dto = createDto("TRANSFER", "1L", "2L", "250.00");
        assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
    }

    @Test
    void deposit_failsIfTargetAccountDoesNotExist() {
        when(productRepository.findByAccountNumber("42L")).thenReturn(Optional.empty());
        TransactionCreateDTO dto = createDto("DEPOSIT", null, "42L", "100.00");
        Exception ex = assertThrows(NotFoundException.class, () -> service.createTransaction(dto));
        assertEquals("Cuenta destino no existe", ex.getMessage());
    }

    @Test
    void withdrawal_failsIfSourceAccountDoesNotExist() {
        when(productRepository.findByAccountNumber("84L")).thenReturn(Optional.empty());
        TransactionCreateDTO dto = createDto("WITHDRAWAL", "84L", null, "50.00");
        Exception ex = assertThrows(NotFoundException.class, () -> service.createTransaction(dto));
        assertEquals("Cuenta origen no existe", ex.getMessage());
    }

    @Test
    void transfer_failsIfSourceAccountDoesNotExist() {
        when(productRepository.findByAccountNumber("1L")).thenReturn(Optional.empty());
        TransactionCreateDTO dto = createDto("TRANSFER", "1L", "2L", "30.00");
        Exception ex = assertThrows(NotFoundException.class, () -> service.createTransaction(dto));
        assertEquals("Cuenta origen no existe", ex.getMessage());
    }

    @Test
    void transfer_failsIfTargetAccountDoesNotExist() {
        Product source = mock(Product.class);
        when(productRepository.findByAccountNumber("1L")).thenReturn(Optional.of(source));
        when(productRepository.findByAccountNumber("2L")).thenReturn(Optional.empty());
        when(source.getBalance()).thenReturn(new BigDecimal("100.00"));
        TransactionCreateDTO dto = createDto("TRANSFER", "1L", "2L", "30.00");
        Exception ex = assertThrows(NotFoundException.class, () -> service.createTransaction(dto));
        assertEquals("Cuenta destino no existe", ex.getMessage());
    }


    @Test
    void shouldThrowIfTransactionDoesntExist(){
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());


        Exception ex = assertThrows(NotFoundException.class, () -> service.findById(1L));
        assertEquals("Transacción no encontrada", ex.getMessage());
    }

    @Test
    void shouldThrowFindByProductIfProductDoesntExist(){

        when(productRepository.findByAccountNumber("2L")).thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> service.findByProductAccountNumber("5301000000"));
        assertEquals("Producto no encontrado", ex.getMessage());



    }



    // --- Helpers ---

    private static TransactionCreateDTO createDto(String type, String source, String target, String amount) {
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType(type);
        dto.setSourceProductId(source);
        dto.setTargetProductId(target);
        dto.setAmount(new BigDecimal(amount));
        dto.setDescription(type + " test");
        return dto;
    }

    private static Stream<Product.Status> invalidAccountStatusProvider() {
        return Stream.of(Product.Status.INACTIVE, Product.Status.CANCELED);
    }
}