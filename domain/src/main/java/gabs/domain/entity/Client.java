package gabs.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;




@Data

@NoArgsConstructor
public class Client {

    private Long id;
    private String identificationType;
    private String identificationNumber;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

   public Client(Long id, String identificationType, String identificationNumber, String firstName, String lastName, String email, LocalDate birthDate, LocalDateTime creationDate, LocalDateTime updateDate) {
       validate(identificationType, identificationNumber, firstName, lastName, email, birthDate);
       this.id = id;
       this.identificationType = identificationType;
       this.identificationNumber = identificationNumber;
       this.firstName = firstName;
       this.lastName = lastName;
       this.email = email;
       this.birthDate = birthDate;
       this.creationDate = creationDate;
       this.updateDate = updateDate;
   }


    private void validate(String identificationType, String identificationNumber,String firstName, String lastName, String email, LocalDate birthDate) {
        if (firstName == null || firstName.length() < 2)
            throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres");
        if (lastName == null || lastName.length() < 2)
            throw new IllegalArgumentException("El apellido debe tener al menos 2 caracteres");
        if (email == null || !Pattern.matches("^[^@]+@[^@]+\\.[^@]+$", email))
            throw new IllegalArgumentException("El email no tiene un formato válido");
        if (birthDate == null || birthDate.isAfter(LocalDate.now().minusYears(18)))
            throw new IllegalArgumentException("El cliente debe ser mayor de edad");
        if(identificationType == null || identificationType.isEmpty())
            throw new IllegalArgumentException("El tipo de identificación no puede estar vacío");
        if (identificationNumber == null || identificationNumber.isEmpty())
            throw new IllegalArgumentException("El número de identificación no puede estar vacío");
        validateIdentificationNumber(identificationNumber);
    }


    private void validateIdentificationNumber(String identificationNumber) {
        if (!Pattern.matches("\\d{7,12}", identificationNumber))
            throw new IllegalArgumentException("La cédula debe tener al menos 7 dígitos numéricos y menos de 12");
    }

}
