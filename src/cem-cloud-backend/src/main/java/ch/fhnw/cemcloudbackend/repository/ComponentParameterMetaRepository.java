package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.ComponentParameterMeta;
import ch.fhnw.cemcloudbackend.entity.ComponentType;
import ch.fhnw.cemcloudbackend.entity.ParameterMeta;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ComponentParameterMetaRepository extends CrudRepository<ComponentParameterMeta, UUID> {
    Iterable<ComponentParameterMeta> findAllByComponentType(ComponentType type);
}
