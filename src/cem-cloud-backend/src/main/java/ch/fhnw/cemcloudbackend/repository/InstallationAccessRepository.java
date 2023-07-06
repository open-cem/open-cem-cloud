package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.InstallationAccess;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface InstallationAccessRepository extends CrudRepository<InstallationAccess, UUID> {
    Iterable<InstallationAccess> findAllByUserEmailAndUserIdIsNull(String email);

    Optional<InstallationAccess> findByInstallationIdAndUserEmail(UUID installationId, String userEmail);
}
