package gabs.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test

    //Made test here in domain because is a business rule
    void savingsAccountCannotHaveNegativeBalance() {

        Product savings = Product.createSavings(1L, false, "5300000001");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                savings.updateBalance(BigDecimal.valueOf(-1))
        );
        assertEquals("La cuenta de ahorros no puede tener saldo menor a 0", ex.getMessage());
    }

    @Test
    void shouldThrowIfSavingsAccountNumberDoesNotStartWith53() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                Product.createSavings(1L, false, "3300000001"));
        assertTrue(ex.getMessage().contains("debe empezar en 53"));
    }

    @Test
    void shouldThrowIfCheckingAccountNumberDoesNotStartWith33() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                Product.createChecking(1L, false, "5300000001"));
        assertTrue(ex.getMessage().contains("debe empezar en 33"));
    }

    @Test
    void shouldThrowIfAccountNumberNotTenDigits_Checking() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                Product.createChecking(1L, false, "33000002") // 8 dígitos
        );
        assertTrue(ex.getMessage().contains("10 dígitos"));
    }

    @Test
    void shouldThrowIfAccountNumberNotTenDigits_Savings() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                Product.createSavings(1L, false, "530000001") // 9 dígitos
        );
        assertTrue(ex.getMessage().contains("10 dígitos"));
    }

    @Test
    void savingsAccountShouldBeActiveByDefault() {
        // Arrange
        Product savings = Product.createSavings(1L, false, "5300000001");

        // Assert
        assertEquals(Product.Status.ACTIVE, savings.getStatus());
    }

    @Test
    void shouldCancelAccountWithZeroBalance() {
        Product savings = Product.createSavings(1L, false, "5300000001");
        savings.updateBalance(BigDecimal.ZERO);
        assertDoesNotThrow(savings::cancel);
        assertEquals(Product.Status.CANCELED, savings.getStatus());
    }

    @Test
    void shouldNotCancelAccountWithPositiveBalance() {
        Product savings = Product.createSavings(1L, false, "5300000002");
        savings.updateBalance(BigDecimal.valueOf(100));
        Exception ex = assertThrows(IllegalStateException.class, savings::cancel);
        assertEquals("Solo se pueden cancelar cuentas con saldo 0", ex.getMessage());
    }

    @Test
    void shouldNotCancelAccountWithNegativeBalance() {
        Product checking = Product.createChecking(1L, false, "3300000001");
        checking.updateBalance(BigDecimal.valueOf(-10));
        Exception ex = assertThrows(IllegalStateException.class, checking::cancel);
        assertEquals("Solo se pueden cancelar cuentas con saldo 0", ex.getMessage());
    }

    @Test
    void creationDateIsAutomaticallySet() {
        Product savings = Product.createSavings(1L, false, "5300000001");
        assertNotNull(savings.getCreationDate());
    }


}