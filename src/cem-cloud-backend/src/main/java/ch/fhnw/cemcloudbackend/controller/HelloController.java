package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.entity.Sensor;
import ch.fhnw.cemcloudbackend.entity.User;
import ch.fhnw.cemcloudbackend.model.set.Set;
import ch.fhnw.cemcloudbackend.mqtt.Mqtt;
import ch.fhnw.cemcloudbackend.repository.SensorRepository;
import ch.fhnw.cemcloudbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
public class HelloController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SensorRepository sensorRepository;

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
    public ResponseEntity<Set> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".yml")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Yaml yaml = new Yaml(new Constructor(Set.class));
        Set set = yaml.load(file.getInputStream());

        Sensor sensor = new Sensor();
        sensor.setId(UUID.randomUUID());
        sensor.setName(set.getSensors()[0].getName());
        sensor.setManufacturer(set.getSensors()[0].getManufacturer());
        sensor.setModel(set.getSensors()[0].getModel());

        sensorRepository.save(sensor);

        return new ResponseEntity<>(set, HttpStatus.OK);
    }

    @PostMapping("/sendEvent")
    public ResponseEntity<String> sendEvent(@RequestParam String message) {
        System.out.println(message);

        Mqtt client = new Mqtt();
        try {

            client.sendMessage("installation/123456789", message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
