package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.ComponentType;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ComponentSubTypeRepository extends CrudRepository<ComponentType, UUID> {
}
