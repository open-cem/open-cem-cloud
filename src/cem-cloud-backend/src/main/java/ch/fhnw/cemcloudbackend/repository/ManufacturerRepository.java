package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.Manufacturer;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ManufacturerRepository extends CrudRepository<Manufacturer, UUID> {
}
