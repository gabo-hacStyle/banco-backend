package gabs.infrastructure.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gabs.application.dto.TransactionCreateDTO;
import gabs.application.ports.TransactionUseCases;
import gabs.domain.entity.Transaction;
import gabs.domain.exceptions.NotFoundException;
import gabs.infrastructure.controller.errors.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TransactionController.class)
@ContextConfiguration(classes = {TransactionController.class, GlobalExceptionHandler.class})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionUseCases transactionService;


    @Autowired
    private ObjectMapper objectMapper;

    private Transaction transactionDeposit;
    private Transaction transactionWithdrawal;
    private Transaction transactionTransfer;
    private TransactionCreateDTO createDTO;
    @BeforeEach
    void setUp() {
        createDTO = new TransactionCreateDTO();
        createDTO.setAmount(new BigDecimal("100.00"));
        createDTO.setDescription("Transaccion de prueba");

        transactionDeposit = new Transaction(
                1L,
                Transaction.Type.DEPOSIT,
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                null,
                "5301000001",
                "Transaccion de prueba"
        );


    }


    // --- HAPPY PATH TESTS ---

    @Test
    void createTransactionDeposit_returnsCreatedTransaction() throws Exception {
        createDTO.setType("DEPOSIT");
        createDTO.setTargetProductId("3301000000");


        when(transactionService.createTransaction(any(TransactionCreateDTO.class))).thenReturn(transactionDeposit);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionDeposit.getId()))
                .andExpect(jsonPath("$.targetProductId").value(transactionDeposit.getTargetProductId()))
                .andExpect(jsonPath("$.type").value(transactionDeposit.getType().name()))
                .andExpect(jsonPath("$.amount").value(transactionDeposit.getAmount().doubleValue()));
    }

    @Test
    void getById_returnsTransactionDeposit() throws Exception {


        when(transactionService.findById(1L)).thenReturn(transactionDeposit);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionDeposit.getId()))
                .andExpect(jsonPath("$.targetProductId").value(transactionDeposit.getTargetProductId()));
    }

    @Test
    void findByProduct_returnsTransactions() throws Exception {

        when(transactionService.findByProductAccountNumber("5301000000")).thenReturn(List.of(transactionDeposit));

        mockMvc.perform(get("/api/transactions/product/5301000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionDeposit.getId()))
                .andExpect(jsonPath("$[0].targetProductId").value(transactionDeposit.getTargetProductId()));
    }

    @Test
    void findAll_returnsTransactions() throws Exception {

        when(transactionService.findAll()).thenReturn(List.of(transactionDeposit));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionDeposit.getId()));
    }

    // --- NOT FOUND TESTS ---

    @Test
    void getById_shouldReturnNotFound_whenTransactionDoesNotExist() throws Exception {
        when(transactionService.findById(123L)).thenThrow(new NotFoundException("Transacción no encontrada"));

        mockMvc.perform(get("/api/transactions/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowWhenDepositToProductDoesntExist() throws Exception {

        createDTO.setType("DEPOSIT");
        createDTO.setTargetProductId("3301000000");

        when(transactionService.createTransaction(createDTO)).thenThrow(new NotFoundException("Cuenta destino no existe"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowWhenWithdrawalFromProductDoesntExist() throws Exception {

        createDTO.setType("WITHDRAWAL");
        createDTO.setTargetProductId("3301000000");

        when(transactionService.createTransaction(createDTO)).thenThrow(new NotFoundException("Cuenta origen no existe"));


        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowNotFoundToProductDoesntExist() throws Exception {

        createDTO.setType("DEPOSIT");
        createDTO.setTargetProductId("3301000000");

        when(transactionService.findByProductAccountNumber("3301000000")).thenThrow(new NotFoundException("Producto no encontrado"));


        mockMvc.perform(get("/api/transactions/product/3301000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isNotFound());
    }


    // --- VALIDATION TESTS ---

    @Test
    void createTransaction_shouldReturnBadRequest_whenValidationFails() throws Exception {
        createDTO.setAmount(BigDecimal.ZERO); // Invalid amount

        when(transactionService.createTransaction(any(TransactionCreateDTO.class)))
                .thenThrow(new IllegalArgumentException("No se puede hacer depósito a una cuenta inactiva o cancelada"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("No se puede hacer depósito a una cuenta inactiva o cancelada"));
    }

}