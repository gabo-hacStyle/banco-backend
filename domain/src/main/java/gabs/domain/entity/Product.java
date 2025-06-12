package gabs.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Product {

    public enum Type { SAVINGS, CHECKING }
    public enum Status { ACTIVE, INACTIVE, CANCELED }

    // Setters (solo id para repositorio)
    @Setter
    private Long id;
    private Type type;
    private String accountNumber;
    private Status status;
    private BigDecimal balance;
    private boolean exemptGMF;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private Long clientId;

    public Product(Long id, Type type, String accountNumber, Status status, BigDecimal balance,
                   boolean exemptGMF, LocalDateTime creationDate, LocalDateTime updateDate, Long clientId) {
        this.id = id;
        this.type = type;
        this.accountNumber = accountNumber;
        this.status = status;
        this.balance = balance;
        this.exemptGMF = exemptGMF;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.clientId = clientId;
    }

    // Factoría genérica para cuentas
    public static Product createChecking(Long clientId, boolean exemptGMF, String accountNumber) {
        validateAccountNumber(accountNumber, "33", "corriente");
        return new Product(
                null, Type.CHECKING, accountNumber, Status.INACTIVE,
                BigDecimal.ZERO, exemptGMF, LocalDateTime.now(), null, clientId
        );
    }

    public static Product createSavings(Long clientId, boolean exemptGMF, String accountNumber) {
        validateAccountNumber(accountNumber, "53", "ahorros");
        return new Product(
                null, Type.SAVINGS, accountNumber, Status.ACTIVE,
                BigDecimal.ZERO, exemptGMF, LocalDateTime.now(), null, clientId
        );
    }

    // Validación reutilizable
    private static void validateAccountNumber(String accountNumber, String prefix, String tipo) {
        if (!accountNumber.startsWith(prefix)) {
            throw new IllegalArgumentException("Número de cuenta de " + tipo + " debe empezar en " + prefix);
        }
        if (accountNumber.length() != 10 || !accountNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("El número de cuenta debe tener exactamente 10 dígitos numéricos");
        }
    }

    public void activate() {
        if (status == Status.CANCELED) throw new IllegalStateException("No se puede activar una cuenta cancelada");
        if (status == Status.ACTIVE) throw new IllegalStateException("Esta cuenta ya está activada");


        this.status = Status.ACTIVE;
        this.updateDate = LocalDateTime.now();
    }
    public void inactivate() {
        if (status == Status.CANCELED) throw new IllegalStateException("No se puede inactivar una cuenta cancelada");
        if (status == Status.INACTIVE) throw new IllegalStateException("Esta cuenta ya está inactiva");

        this.status = Status.INACTIVE;
        this.updateDate = LocalDateTime.now();
    }

    public void cancel() {
        if (this.balance.compareTo(BigDecimal.ZERO) != 0)
            throw new IllegalStateException("Solo se pueden cancelar cuentas con saldo 0");
        this.status = Status.CANCELED;
        this.updateDate = LocalDateTime.now();
    }

    public void updateBalance(BigDecimal amount) {
        if (type == Type.SAVINGS && amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("La cuenta de ahorros no puede tener saldo menor a 0");
        this.balance = amount;
        this.updateDate = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product account)) return false;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}