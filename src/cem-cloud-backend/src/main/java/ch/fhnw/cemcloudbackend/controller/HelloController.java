package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.entity.User;
import ch.fhnw.cemcloudbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class HelloController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public User getHello() {
        Optional<User> user = userRepository.findById(1);
        User u;
        if (user.isEmpty()) {
            u = new User();
            u.setId(1);
            u.setName("Testuser");
            userRepository.save(u);
        } else {
            u = user.get();
        }

        return u;
    }
}
