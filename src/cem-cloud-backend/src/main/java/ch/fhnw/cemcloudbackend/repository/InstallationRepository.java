package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.model.Role;
import ch.fhnw.cemcloudbackend.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface InstallationRepository extends CrudRepository<Installation, UUID> {
    Optional<Installation> findByInstallationAccessesUserId(UUID userId);
    Iterable<Installation> findAllByInstallationAccessesUserId(UUID userId);

    default Optional<Installation> getInstallation(UUID id, User user) {
        return user.isInRole(Role.ADMINISTRATOR)
                ? findById(id)
                : findByInstallationAccessesUserId(user.getId());
    }

    default Iterable<Installation> getInstallations(User user) {
        return user.isInRole(Role.ADMINISTRATOR)
                ? findAll()
                : findAllByInstallationAccessesUserId(user.getId());
    }
}
