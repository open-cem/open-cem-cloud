package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
}
