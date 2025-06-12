package gabs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateClientDTO {


    public String identificationType;
    public String identificationNumber;
    public String firstName;
    public String lastName;
    public String email;
    public LocalDate birthDate;


}