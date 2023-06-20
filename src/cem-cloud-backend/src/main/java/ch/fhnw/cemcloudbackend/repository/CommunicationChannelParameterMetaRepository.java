package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.CommunicationChannelParameterMeta;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CommunicationChannelParameterMetaRepository extends CrudRepository<CommunicationChannelParameterMeta, UUID> {
    Iterable<CommunicationChannelParameterMeta> findAllByCommunicationChannelTypeId(UUID id);
}
