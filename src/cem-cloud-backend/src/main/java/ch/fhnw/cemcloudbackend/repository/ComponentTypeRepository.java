package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.ComponentType;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ComponentTypeRepository extends CrudRepository<ComponentType, UUID> {
}
