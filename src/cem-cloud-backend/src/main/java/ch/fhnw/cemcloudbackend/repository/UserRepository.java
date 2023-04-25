package ch.fhnw.cemcloudbackend.repository;

import ch.fhnw.cemcloudbackend.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
