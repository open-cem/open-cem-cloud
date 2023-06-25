package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.CommunicationChannel;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CommunicationChannelRepository extends CrudRepository<CommunicationChannel, UUID> {
    Iterable<CommunicationChannel> findAllByInstallationId(UUID installationId);
}
