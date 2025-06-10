package gabs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionCreateDTO {
    private String type;
    private BigDecimal amount;
    private Long sourceProductId;
    private Long targetProductId;
    private String description;
}
