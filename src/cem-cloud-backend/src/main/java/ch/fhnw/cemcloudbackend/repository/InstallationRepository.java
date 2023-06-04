package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.Installation;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface InstallationRepository extends CrudRepository<Installation, UUID> {
}
