package gabs.infrastructure.repository.entity;

import gabs.domain.entity.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import lombok.Data;

@Data
@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(columnNames = "accountNumber"))
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Product.Type type;

    @Column(length = 10, unique = true, nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private Product.Status status;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private boolean exemptGMF;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    private LocalDateTime updateDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_client"))
    private ClientEntity client;
}
