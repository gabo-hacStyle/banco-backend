package gabs.application.service;

import gabs.application.dto.ProductCreateDTO;
import gabs.application.dto.TransactionCreateDTO;
import gabs.domain.entity.Product;
import gabs.domain.entity.Transaction;
import gabs.domain.ports.ProductRepository;
import gabs.domain.ports.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private TransactionService service;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        productRepository = mock(ProductRepository.class);
        service = new TransactionService(transactionRepository, productRepository);
    }

    @Test
    void shouldThrowIfTypeInvalid() {
        long targetId = 2L;
        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("OTRO");
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setTargetProductId(targetId);
        dto.setDescription("Consignación test");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
        assertEquals("Tipo de transacción no soportado", ex.getMessage());

    }

    @Test
    void createsDeposit_andUpdatesBalance() {
        // Arrange
        long targetId = 2L;
        Product target = mock(Product.class);
        when(productRepository.findById(targetId)).thenReturn(Optional.of(target));
        when(target.getBalance()).thenReturn(new BigDecimal("1000.00"));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("DEPOSIT");
        dto.setAmount(new BigDecimal("1000.00"));
        dto.setTargetProductId(targetId);
        dto.setDescription("Consignación test");

        // Act
        Transaction tx = service.createTransaction(dto);

        // Assert: verifica que el saldo actualizado fue 2000
        ArgumentCaptor<BigDecimal> balanceCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(target).updateBalance(balanceCaptor.capture());
        assertEquals(new BigDecimal("2000.00"), balanceCaptor.getValue());

        verify(productRepository).save(target);
        verify(transactionRepository, times(1)).save(any(Transaction.class));

        // El amount de la transacción es el monto de la consignación: 1000
        assertEquals(Transaction.Type.DEPOSIT, tx.getType());
        assertEquals(new BigDecimal("1000.00"), tx.getAmount());
        assertEquals("Consignación test", tx.getDescription());

        assertNull(tx.getSourceProductId());
    }

    @Test
    void createsWithdrawal_andUpdatesBalance() {
        long sourceId = 10L;
        Product source = mock(Product.class);

        // Saldo original de la cuenta
        when(productRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(source.getBalance()).thenReturn(new BigDecimal("300.00"));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("WITHDRAWAL");
        dto.setAmount(new BigDecimal("100.00"));
        dto.setSourceProductId(sourceId);
        dto.setDescription("Retiro test");

        Transaction tx = service.createTransaction(dto);

        // Captura el saldo actualizado
        ArgumentCaptor<BigDecimal> balanceCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(source).updateBalance(balanceCaptor.capture());
        assertEquals(new BigDecimal("200.00"), balanceCaptor.getValue()); // 300 - 100

        verify(productRepository).save(source);
        verify(transactionRepository, times(1)).save(any(Transaction.class));

        assertEquals(Transaction.Type.WITHDRAWAL, tx.getType());
        assertEquals(new BigDecimal("100.00"), tx.getAmount());
        assertEquals("Retiro test", tx.getDescription());

        assertNull(tx.getTargetProductId());
    }
    @Test
    void createsTransfer_andUpdatesBalance() {
        long sourceId = 1L, targetId = 2L;
        Product source = mock(Product.class);
        Product target = mock(Product.class);

        // Saldos originales
        when(productRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(productRepository.findById(targetId)).thenReturn(Optional.of(target));
        when(source.getBalance()).thenReturn(new BigDecimal("400.00"));
        when(target.getBalance()).thenReturn(new BigDecimal("50.00"));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("TRANSFER");
        dto.setAmount(new BigDecimal("150.00"));
        dto.setSourceProductId(sourceId);
        dto.setTargetProductId(targetId);
        dto.setDescription("Transfer test");

        Transaction tx = service.createTransaction(dto);

        // Captura los saldos actualizados
        ArgumentCaptor<BigDecimal> sourceCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> targetCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        verify(source).updateBalance(sourceCaptor.capture());
        verify(target).updateBalance(targetCaptor.capture());

        assertEquals(new BigDecimal("250.00"), sourceCaptor.getValue()); // 400 - 150
        assertEquals(new BigDecimal("200.00"), targetCaptor.getValue()); // 50 + 150

        verify(productRepository).save(source);
        verify(productRepository).save(target);

        // Se generan dos movimientos: debito y crédito, pero el método retorna el de crédito
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        assertEquals(Transaction.Type.TRANSFER, tx.getType());
        assertEquals(new BigDecimal("150.00"), tx.getAmount());


    }

    @Test
    void withdrawal_withSufficientBalance_success() {
        // Arrange
        long sourceId = 1L;
        Product source = mock(Product.class);
        when(productRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(source.getBalance()).thenReturn(new BigDecimal("1000.00"));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("WITHDRAWAL");
        dto.setAmount(new BigDecimal("200.00"));
        dto.setSourceProductId(sourceId);
        dto.setDescription("Retiro test");

        // Act
        Transaction tx = service.createTransaction(dto);

        // Assert
        verify(source).updateBalance(new BigDecimal("800.00"));
        verify(productRepository).save(source);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertEquals(Transaction.Type.WITHDRAWAL, tx.getType());
        assertEquals(new BigDecimal("200.00"), tx.getAmount());

        assertNull(tx.getTargetProductId());
    }

    @Test
    void withdrawal_withInsufficientBalance_throws() {
        long sourceId = 1L;
        Product source = mock(Product.class);
        when(productRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(source.getBalance()).thenReturn(new BigDecimal("50.00"));

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("WITHDRAWAL");
        dto.setAmount(new BigDecimal("200.00"));
        dto.setSourceProductId(sourceId);
        dto.setDescription("Intento de retiro fallido");

        assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
    }



    @Test
    void transfer_withInsufficientBalance_throws() {
        long sourceId = 1L, targetId = 2L;
        Product source = mock(Product.class);
        Product target = mock(Product.class);
        when(productRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(productRepository.findById(targetId)).thenReturn(Optional.of(target));
        when(source.getBalance()).thenReturn(new BigDecimal("100.00"));

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("TRANSFER");
        dto.setAmount(new BigDecimal("250.00"));
        dto.setSourceProductId(sourceId);
        dto.setTargetProductId(targetId);
        dto.setDescription("Transferencia insuficiente");

        assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
    }

    @Test
    void transfer_toNonExistentAccount_throws() {
        long sourceId = 1L, targetId = 99L;
        Product source = mock(Product.class);
        when(productRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(productRepository.findById(targetId)).thenReturn(Optional.empty());
        when(source.getBalance()).thenReturn(new BigDecimal("500.00"));

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("TRANSFER");
        dto.setAmount(new BigDecimal("250.00"));
        dto.setSourceProductId(sourceId);
        dto.setTargetProductId(targetId);
        dto.setDescription("Transferencia destino no existe");

        assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
    }

    @Test
    void deposit_failsIfTargetAccountDoesNotExist() {
        long targetId = 42L;
        when(productRepository.findById(targetId)).thenReturn(Optional.empty());

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("DEPOSIT");
        dto.setAmount(new BigDecimal("100.00"));
        dto.setTargetProductId(targetId);
        dto.setDescription("Intento de consignación");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
        assertEquals("Cuenta destino no existe", ex.getMessage());
    }

    @Test
    void withdrawal_failsIfSourceAccountDoesNotExist() {
        long sourceId = 84L;
        when(productRepository.findById(sourceId)).thenReturn(Optional.empty());

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("WITHDRAWAL");
        dto.setAmount(new BigDecimal("50.00"));
        dto.setSourceProductId(sourceId);
        dto.setDescription("Intento de retiro");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
        assertEquals("Cuenta origen no existe", ex.getMessage());
    }

    @Test
    void transfer_failsIfSourceAccountDoesNotExist() {
        long sourceId = 1L, targetId = 2L;
        when(productRepository.findById(sourceId)).thenReturn(Optional.empty());
        // No importa si el target existe o no, el source se valida primero

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("TRANSFER");
        dto.setAmount(new BigDecimal("30.00"));
        dto.setSourceProductId(sourceId);
        dto.setTargetProductId(targetId);
        dto.setDescription("Transferencia fallida");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
        assertEquals("Cuenta origen no existe", ex.getMessage());
    }

    @Test
    void transfer_failsIfTargetAccountDoesNotExist() {
        long sourceId = 1L, targetId = 2L;
        Product source = mock(Product.class);
        when(productRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(productRepository.findById(targetId)).thenReturn(Optional.empty());
        when(source.getBalance()).thenReturn(new BigDecimal("100.00"));

        TransactionCreateDTO dto = new TransactionCreateDTO();
        dto.setType("TRANSFER");
        dto.setAmount(new BigDecimal("30.00"));
        dto.setSourceProductId(sourceId);
        dto.setTargetProductId(targetId);
        dto.setDescription("Transferencia fallida");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.createTransaction(dto));
        assertEquals("Cuenta destino no existe", ex.getMessage());
    }
}