package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.MqttAccessControl;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MqttAccessControlRepository extends CrudRepository<MqttAccessControl, UUID> {
}
