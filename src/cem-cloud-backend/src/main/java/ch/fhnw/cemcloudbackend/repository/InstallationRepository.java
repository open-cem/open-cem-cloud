package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.model.Role;
import ch.fhnw.cemcloudbackend.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface InstallationRepository extends CrudRepository<Installation, UUID> {
    Optional<Installation> findByInstallationAccessesUserIdAndId(UUID userId, UUID installationId);

    Optional<Installation> findBySerialNumber(String serialNumber);

    Iterable<Installation> findAllByInstallationAccessesUserId(UUID userId);

    default Optional<Installation> getInstallation(UUID id, User user) {
        return user.isInAnyRole(Role.ADMINISTRATOR, Role.INSTALLATEUR)
                ? findById(id)
                : findByInstallationAccessesUserIdAndId(user.getId(), id);
    }

    default Iterable<Installation> getInstallations(User user) {
        return user.isInAnyRole(Role.ADMINISTRATOR, Role.INSTALLATEUR)
                ? findAll()
                : findAllByInstallationAccessesUserId(user.getId());
    }
}
