package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.SmartGridreadyDefinition;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SmartGridreadyRepository extends CrudRepository<SmartGridreadyDefinition, UUID> {
}
