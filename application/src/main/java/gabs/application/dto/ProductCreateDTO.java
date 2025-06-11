package gabs.application.dto;


import gabs.domain.entity.Product;
import lombok.Data;

@Data
public class ProductCreateDTO {
    private String  type;
    private boolean exemptGMF;
    private Long clientId;

}
