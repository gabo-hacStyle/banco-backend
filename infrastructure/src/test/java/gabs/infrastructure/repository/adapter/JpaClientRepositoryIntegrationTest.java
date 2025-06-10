package gabs.infrastructure.repository.adapter;

import gabs.domain.entity.Client;
import gabs.domain.ports.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;




import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class JpaClientRepositoryIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    private Client buildClient() {
        return new Client(
                null,
                "CC",
                "11223344",
                "Gabriel",
                "Hernandez",
                "gabriel@mail.com",
                LocalDate.of(1990, 3, 21),
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void saveAndFindClient() {
        Client client = buildClient();
        Client saved = clientRepository.save(client);

        assertThat(saved.getId()).isNotNull();

        Optional<Client> found = clientRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Gabriel");
    }

    @Test
    void existsByIdentificationNumberReturnsTrue() {
        Client client = buildClient();
        clientRepository.save(client);

        boolean exists = clientRepository.existsByIdentificationNumber("11223344");
        assertThat(exists).isTrue();
    }

    @Test
    void deleteByIdRemovesClient() {
        Client client = buildClient();
        Client saved = clientRepository.save(client);

        clientRepository.deleteById(saved.getId());
        assertThat(clientRepository.findById(saved.getId())).isEmpty();
    }
}
