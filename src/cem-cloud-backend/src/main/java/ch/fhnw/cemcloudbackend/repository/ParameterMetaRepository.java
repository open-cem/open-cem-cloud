package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.ParameterMeta;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ParameterMetaRepository extends CrudRepository<ParameterMeta, UUID> {
}
