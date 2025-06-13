package gabs.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabs.application.dto.ProductCreateDTO;
import gabs.application.ports.ProductUseCases;
import gabs.domain.entity.Product;
import gabs.domain.exceptions.NotFoundException;
import gabs.infrastructure.controller.errors.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.match.ContentRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;


import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = {ProductController.class, GlobalExceptionHandler.class})
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductUseCases productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product productSaving;
    private Product productChecking;
    private ProductCreateDTO createDTOSavings;

    @BeforeEach
    void setUp() {



        productSaving = Product.createSavings(1L, true, "5301000000");
        productChecking = Product.createChecking(1L, true, "3301000000");
    }

    // --- HAPPY PATH TESTS ---

    @Test
    void createProduct_returnsCreatedProductSavings() throws Exception {
        ProductCreateDTO createDTOSavings = new ProductCreateDTO();
        createDTOSavings.setClientId(1L);
        createDTOSavings.setType("SAVINGS");
        createDTOSavings.setExemptGMF(true);

        when(productService.createProduct(any(ProductCreateDTO.class))).thenReturn(productSaving);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTOSavings)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productSaving.getId()))
                .andExpect(jsonPath("$.type").value(productSaving.getType().name()))
                .andExpect(jsonPath("$.accountNumber").value(productSaving.getAccountNumber()));
    }

    @Test
    void createProduct_returnsCreatedProductChecking() throws Exception {

        ProductCreateDTO createDTOChecking = new ProductCreateDTO();
        createDTOChecking.setClientId(1L);
        createDTOChecking.setType("CHECKING");
        createDTOChecking.setExemptGMF(true);

        when(productService.createProduct(any(ProductCreateDTO.class))).thenReturn(productChecking);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTOChecking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productChecking.getId()))
                .andExpect(jsonPath("$.type").value(productChecking.getType().name()))
                .andExpect(jsonPath("$.accountNumber").value(productChecking.getAccountNumber()));
    }



    @Test
    void getById_returnsProductSaving() throws Exception {
        when(productService.findByAccountNumber("5301000000")).thenReturn(productSaving);

        mockMvc.perform(get("/api/products/5301000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productSaving.getId()))
                .andExpect(jsonPath("$.type").value(productSaving.getType().name()));
    }

    @Test
    void getById_returnsProductChecking() throws Exception {
        when(productService.findByAccountNumber("3301000000")).thenReturn(productChecking);

        mockMvc.perform(get("/api/products/3301000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productChecking.getId()))
                .andExpect(jsonPath("$.type").value(productChecking.getType().name()));
    }

    @Test
    void getByClient_returnsProducts() throws Exception {
        when(productService.findByClientId(1L)).thenReturn(List.of(productSaving));

        mockMvc.perform(get("/api/products/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(productSaving.getId()))
                .andExpect(jsonPath("$[0].accountNumber").value(productSaving.getAccountNumber()));
    }


    @Test
    void enableProduct_returnsProduct() throws Exception {
        when(productService.activateProduct("5301000000")).thenReturn(productSaving);

        mockMvc.perform(put("/api/products/5301000000/enable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void disableProduct_returnsProduct() throws Exception {
        productSaving.inactivate(); // Cambia a INACTIVE
        when(productService.inactivateProduct("5301000000")).thenReturn(productSaving);

        mockMvc.perform(put("/api/products/5301000000/disable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }


    @Test
    void cancelProduct_returnsProduct() throws Exception {
        productSaving.cancel();
        when(productService.cancelProduct("5301000000")).thenReturn(productSaving);

        mockMvc.perform(put("/api/products/5301000000/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }

    // --- NOT FOUND TESTS ---

    @Test
    void getById_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        when(productService.findByAccountNumber("5301000000")).thenThrow(new NotFoundException("Producto no encontrado"));

        mockMvc.perform(get("/api/products/5301000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void enableProduct_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        when(productService.activateProduct("5301000000")).thenThrow(new NotFoundException("Producto no encontrado"));

        mockMvc.perform(put("/api/products/5301000000/enable"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Producto no encontrado"));
    }

    @Test
    void disableProduct_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        when(productService.inactivateProduct("5301000000")).thenThrow(new NotFoundException("Producto no encontrado"));

        mockMvc.perform(put("/api/products/5301000000/disable"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Producto no encontrado"));
    }

    @Test
    void cancelProduct_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        when(productService.cancelProduct("5301000000")).thenThrow(new NotFoundException("Producto no encontrado"));

        mockMvc.perform(put("/api/products/5301000000/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Producto no encontrado"));
    }

    @Test
    void getByClient_shouldReturnNotFound_whenClientDoesNotExist() throws Exception {
        when(productService.findByClientId(1L)).thenThrow(new NotFoundException("Cliente no existe"));
        mockMvc.perform(get("/api/products/client/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Cliente no existe"));

    }

    // --- VALIDATION TESTS ---

    @Test
    void createProduct_shouldReturnBadRequest_whenValidationFails() throws Exception {
        ProductCreateDTO createDTOSavings = new ProductCreateDTO();
        createDTOSavings.setClientId(1L);
        createDTOSavings.setType("SAVINGS");
        createDTOSavings.setExemptGMF(true);

        when(productService.createProduct(any(ProductCreateDTO.class)))
                .thenThrow(new IllegalArgumentException("El cliente ya tiene una cuenta exenta de GMF"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTOSavings)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("El cliente ya tiene una cuenta exenta de GMF"));
    }



}