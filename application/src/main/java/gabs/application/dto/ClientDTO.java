package gabs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    public Long id;
    public String identificationType;
    public String identificationNumber;
    public String firstName;
    public String lastName;
    public String email;
    public LocalDate birthDate;
    public LocalDateTime creationDate;
    public LocalDateTime updateDate;

}
