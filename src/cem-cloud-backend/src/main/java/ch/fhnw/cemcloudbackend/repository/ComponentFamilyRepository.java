package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.ComponentFamily;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ComponentFamilyRepository extends CrudRepository<ComponentFamily, UUID> {
}
