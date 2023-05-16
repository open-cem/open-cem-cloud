package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.HardwareComponentType;
import org.springframework.data.repository.CrudRepository;

public interface HardwareComponentTypeRepository extends CrudRepository<HardwareComponentType, String> {
}
