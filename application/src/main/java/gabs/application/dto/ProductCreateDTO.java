package gabs.application.dto;


import lombok.Data;

@Data
public class ProductCreateDTO {
    private String type;
    private boolean exemptGMF;
    private Long clientId;

}
