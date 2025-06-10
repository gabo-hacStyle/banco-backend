package gabs.domain.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;



//This is a test class for the Client entity in the domain layer of the application.
//Unit tests to validate the class, this will ensure better chances to avoid errors in the
//services and controllers test
public class ClientTest {


    // Test cases for the Client entity


    // Test for the constructor with invalid data, it means validations
    @Test
    public void shouldThrowWhenUnderage() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Client(
                        null,
                        "CC",
                        "1234567",
                        "Juan",
                        "Perez",
                        "juan@mail.com",
                        LocalDate.now().minusYears(17),
                        LocalDateTime.now(),
                        null)
        );
        Assertions.assertEquals("El cliente debe ser mayor de edad", exception.getMessage());
    }



   @Test
    public void shouldThrowWhenInvalidEmail() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Client(
                        null,
                        "CC",
                        "1234567",
                        "Juan",
                        "Perez",
                        "juanmail.com", // Invalid email format
                        LocalDate.now().minusYears(25),
                        LocalDateTime.now(),
                        null)
        );
        Assertions.assertEquals("El email no tiene un formato válido", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenFirstNameTooShort() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Client(
                        null,
                        "CC",
                        "1234567",
                        "J",
                        "Perez",
                        "juan@mail.com",
                        LocalDate.now().minusYears(25),
                        LocalDateTime.now(),
                        null)
        );
        Assertions.assertEquals("El nombre debe tener al menos 2 caracteres", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenLastNameTooShort() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Client(
                        null,
                        "CC",
                        "1234567",
                        "Juan",
                        "P",
                        "juan@mail.com",
                        LocalDate.now().minusYears(25),
                        LocalDateTime.now(),
                        null)
        );
        Assertions.assertEquals("El apellido debe tener al menos 2 caracteres", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenEmailIsNull() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Client(
                        null,
                        "CC",
                        "1234567",
                        "Juan",
                        "Perez",
                        null,
                        LocalDate.now().minusYears(25),
                        LocalDateTime.now(),
                        null)
        );
        Assertions.assertEquals("El email no tiene un formato válido", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenIdentificationTypeIsNullOrEmpty() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Client(
                        null,
                        "",
                        "1234567",
                        "Juan",
                        "Perez",
                        "juan@mail.com",
                        LocalDate.now().minusYears(25),
                        LocalDateTime.now(),
                        null)
        );
        Assertions.assertEquals("El tipo de identificación no puede estar vacío", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenIdentificationNumberIsNullOrEmpty() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Client(
                        null,
                        "CC",
                        "",
                        "Juan",
                        "Perez",
                        "juan@mail.com",
                        LocalDate.now().minusYears(25),
                        LocalDateTime.now(),
                        null)
        );
        Assertions.assertEquals("El número de identificación no puede estar vacío", exception.getMessage());
    }

    @Test
    public void shouldThrowWhenIdentificationNumberInvalidFormat() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Client(
                        null,
                        "CC",
                        "abc1234",
                        "Juan",
                        "Perez",
                        "juan@mail.com",
                        LocalDate.now().minusYears(25),
                        LocalDateTime.now(),
                        null)
        );
        Assertions.assertEquals("La cédula debe tener al menos 7 dígitos numéricos y menos de 12", exception.getMessage());
    }


    //test for the constructor

    @Test
    public void shouldCreateValidClient() {
        Client c = new Client(
                null,
                "CC",
                "1234567",
                "Juan",
                "Perez",
                "juan@mail.com",
                LocalDate.now().minusYears(25),
                LocalDateTime.now(),
                null);
        Assertions.assertEquals("Juan", c.getFirstName());
    }




}