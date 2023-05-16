package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.DeviceType;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DeviceTypeRepository extends CrudRepository<DeviceType, UUID> {
}
