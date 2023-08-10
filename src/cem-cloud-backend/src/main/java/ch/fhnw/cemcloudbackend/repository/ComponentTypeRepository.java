package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.ComponentType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ComponentTypeRepository extends CrudRepository<ComponentType, UUID> {
    Optional<ComponentType> findByCode(String code);
}
