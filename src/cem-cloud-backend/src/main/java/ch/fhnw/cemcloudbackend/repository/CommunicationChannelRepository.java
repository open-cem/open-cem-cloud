package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.CommunicationChannel;
import ch.fhnw.cemcloudbackend.model.Role;
import ch.fhnw.cemcloudbackend.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommunicationChannelRepository extends CrudRepository<CommunicationChannel, UUID> {
    Optional<CommunicationChannel> findByInstallationInstallationAccessesUserIdAndId(UUID userId, UUID id);

    default Optional<CommunicationChannel> getChannel(UUID id, User user) {
        return user.isInAnyRole(Role.ADMINISTRATOR, Role.INSTALLATEUR)
                ? findById(id)
                : findByInstallationInstallationAccessesUserIdAndId(user.getId(), id);
    }
}
