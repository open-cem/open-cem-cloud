package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.Sensor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SensorRepository extends CrudRepository<Sensor, UUID> {
}
