package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.model.Role;
import ch.fhnw.cemcloudbackend.model.User;
import ch.fhnw.cemcloudbackend.repository.WizardSetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("images")
    public ResponseEntity<Iterable<String>> getAllImages(JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(sets.findAllImages());
    }

    @DeleteMapping("{name}")
    public ResponseEntity<Void> delete(@PathVariable String name, JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (sets.find(name).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        sets.delete(name);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("images/{name}")
    public ResponseEntity<Void> deleteImage(@PathVariable String name, JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (sets.findImage(name).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        sets.deleteImage(name);

        return ResponseEntity.noContent().build();
    }
}
