package gabs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateDTO {
    private String type;
    private BigDecimal amount;
    private String sourceProductId;
    private String targetProductId;
    private String description;
}
