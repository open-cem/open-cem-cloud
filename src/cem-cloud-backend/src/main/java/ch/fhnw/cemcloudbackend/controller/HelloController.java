package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.entity.User;
import ch.fhnw.cemcloudbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@RestController
public class HelloController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public User getHello(JwtAuthenticationToken auth) {
        Optional<User> user = userRepository.findById(UUID.fromString(auth.getName()));
        User u;
        if (user.isEmpty()) {
            u = new User();
            u.setId(UUID.fromString(auth.getName()));
            u.setName(auth.getTokenAttributes().get("name").toString());
            userRepository.save(u);
        } else {
            u = user.get();
        }

        return u;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {

        return new ResponseEntity<>(file.getOriginalFilename(), HttpStatus.OK);
    }
}
