package gabs.infrastructure.repository.springdata;

import gabs.infrastructure.repository.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataClientRepository extends JpaRepository<ClientEntity, Long> {
    boolean existsByIdentificationNumber(String identificationNumber);
}
