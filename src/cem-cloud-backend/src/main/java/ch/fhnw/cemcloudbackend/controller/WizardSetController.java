package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.model.Role;
import ch.fhnw.cemcloudbackend.model.User;
import ch.fhnw.cemcloudbackend.repository.WizardSetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("sets")
public class WizardSetController extends BaseController {

    private WizardSetRepository sets;

    public WizardSetController(WizardSetRepository sets) {
        this.sets = sets;
    }

    @GetMapping()
    public ResponseEntity<Iterable<String>> getAll(JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(sets.findAll());
    }

    @GetMapping("/images")
    public ResponseEntity<Iterable<String>> getAllImages(JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(sets.findAllImages());
    }
}
