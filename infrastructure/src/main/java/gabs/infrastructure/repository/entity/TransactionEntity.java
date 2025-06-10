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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transaction.Status status;

    @ManyToOne
    @JoinColumn(name = "source_product_id", foreignKey = @ForeignKey(name = "fk_transaction_source_product"))
    private ProductEntity sourceProduct;

    @ManyToOne
    @JoinColumn(name = "target_product_id", foreignKey = @ForeignKey(name = "fk_transaction_target_product"))
    private ProductEntity targetProduct;

    private String description;

}
