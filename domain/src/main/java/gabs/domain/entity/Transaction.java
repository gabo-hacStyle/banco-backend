package gabs.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Transaction {

    public enum Type {DEPOSIT, WITHDRAWAL, TRANSFER}

    public enum Status {SUCCESS, FAILED}


    private Long id;
    private Type type;
    private BigDecimal amount;
    private LocalDateTime date;
    private Status status;

    private Long sourceProductId; // cuenta origen (retiro o transferencia)
    private Long targetProductId; // cuenta destino (transferencia)

    private String description;

    public Transaction(Long id, Type type, BigDecimal amount, LocalDateTime date, Status status,
                       Long sourceProductId, Long targetProductId, String description) {

        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date != null ? date : LocalDateTime.now();
        this.status = status;
        this.sourceProductId = sourceProductId;
        this.targetProductId = targetProductId;
        this.description = description;
    }

    // Setters para repo (solo id)
    public void setId(Long id) { this.id = id; }
}
