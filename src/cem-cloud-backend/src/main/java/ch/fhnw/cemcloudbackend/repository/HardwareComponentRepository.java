package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.HardwareComponent;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface HardwareComponentRepository extends CrudRepository<HardwareComponent, UUID> {
    Iterable<HardwareComponent> findAllByTypeComponentFamilyName(String name);
}
