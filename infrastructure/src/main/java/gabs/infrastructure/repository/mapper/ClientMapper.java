package gabs.infrastructure.repository.mapper;

import gabs.domain.entity.Client;
import gabs.infrastructure.repository.entity.ClientEntity;

public class ClientMapper {

    //Manual mapping between Client and ClientEntity
    //Not using libraries like MapStruct for simplicity
    public static ClientEntity toEntity(Client client) {
        if (client == null) return null;
        ClientEntity entity = new ClientEntity();
        entity.setId(client.getId());
        entity.setIdentificationType(client.getIdentificationType());
        entity.setIdentificationNumber(client.getIdentificationNumber());
        entity.setFirstName(client.getFirstName());
        entity.setLastName(client.getLastName());
        entity.setEmail(client.getEmail());
        entity.setBirthDate(client.getBirthDate());
        entity.setCreationDate(client.getCreationDate());
        entity.setUpdateDate(client.getUpdateDate());
        return entity;
    }

    public static Client toDomain(ClientEntity entity) {
        if (entity == null) return null;
        return new Client(
                entity.getId(),
                entity.getIdentificationType(),
                entity.getIdentificationNumber(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getBirthDate(),
                entity.getCreationDate(),
                entity.getUpdateDate()
        );
    }

}
