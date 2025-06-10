package gabs.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


@NoArgsConstructor
public class Product {

    public enum Type { SAVINGS, CHECKING }
    public enum Status { ACTIVE, INACTIVE, CANCELED }

    private Long id;
    private Type type;
    private String accountNumber;
    private Status status;
    private BigDecimal balance;
    private boolean exemptGMF;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private Long clientId; // Id del cliente propietario

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

    // Factoría para crear cuenta corriente
    public static Product createChecking(Long clientId, boolean exemptGMF) {
        String number = ProductNumberGenerator.generateCheckingAccountNumber();
        return new Product(
                null,
                Type.CHECKING,
                number,
                Status.INACTIVE,
                BigDecimal.ZERO,
                exemptGMF,
                LocalDateTime.now(),
                null,
                clientId
        );
    }

    // Factoría para crear cuenta de ahorros
    public static Product createSavings(Long clientId, boolean exemptGMF) {
        String number = ProductNumberGenerator.generateSavingsAccountNumber();
        return new Product(
                null,
                Type.SAVINGS,
                number,
                Status.ACTIVE,
                BigDecimal.ZERO,
                exemptGMF,
                LocalDateTime.now(),
                null,
                clientId
        );
    }

    // Activar o inactivar cuenta
    public void activate() {
        if (status == Status.CANCELED) throw new IllegalStateException("No se puede activar una cuenta cancelada");
        this.status = Status.ACTIVE;
        this.updateDate = LocalDateTime.now();
    }
    public void inactivate() {
        if (status == Status.CANCELED) throw new IllegalStateException("No se puede inactivar una cuenta cancelada");
        this.status = Status.INACTIVE;
        this.updateDate = LocalDateTime.now();
    }

    // Cancelar solo si saldo == 0
    public void cancel() {
        if (this.balance.compareTo(BigDecimal.ZERO) != 0)
            throw new IllegalStateException("Solo se pueden cancelar cuentas con saldo 0");
        this.status = Status.CANCELED;
        this.updateDate = LocalDateTime.now();
    }

    // Actualizar saldo (solo permitido si nueva regla lo permite)
    public void updateBalance(BigDecimal amount) {
        if (type == Type.SAVINGS && amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("La cuenta de ahorros no puede tener saldo menor a 0");
        this.balance = amount;
        this.updateDate = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public Type getType() { return type; }
    public String getAccountNumber() { return accountNumber; }
    public Status getStatus() { return status; }
    public BigDecimal getBalance() { return balance; }
    public boolean isExemptGMF() { return exemptGMF; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public LocalDateTime getUpdateDate() { return updateDate; }
    public Long getClientId() { return clientId; }

    // Setters (solo id para repositorio)
    public void setId(Long id) { this.id = id; }

    // equals/hashCode solo por id y numero de cuenta
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product account = (Product) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }


}
