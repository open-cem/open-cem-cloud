package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.Model;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ModelRepository extends CrudRepository<Model, UUID> {
}
