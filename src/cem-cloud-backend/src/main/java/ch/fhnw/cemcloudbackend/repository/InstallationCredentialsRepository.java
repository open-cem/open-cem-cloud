package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.InstallationCredentials;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface InstallationCredentialsRepository extends CrudRepository<InstallationCredentials, UUID> {
    Optional<InstallationCredentials> findBySerialNumber(String serialNumber);
}
