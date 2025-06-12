package gabs.infrastructure.repository.entity;

import gabs.domain.entity.Transaction;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Entity
@Table(name = "transactions")
@Data
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transaction.Type type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime date;


    // Cambia de relación a campo String + FK manual
    @Column(name = "source_account_number", length = 10)
    private String sourceAccountNumber;

    @Column(name = "target_account_number", length = 10)
    private String targetAccountNumber;

    private String description;

}
