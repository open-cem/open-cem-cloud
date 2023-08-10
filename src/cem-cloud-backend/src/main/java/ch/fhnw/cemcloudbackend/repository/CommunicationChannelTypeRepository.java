package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.CommunicationChannelType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommunicationChannelTypeRepository extends CrudRepository<CommunicationChannelType, UUID> {
    Optional<CommunicationChannelType> findByCode(String code);
}
