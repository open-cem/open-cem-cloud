package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.model.Role;
import ch.fhnw.cemcloudbackend.model.User;
import ch.fhnw.cemcloudbackend.repository.WizardSetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<Void> post(@RequestParam(name = "file") List<MultipartFile> files,
                                     JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String filename = file.getOriginalFilename();
                if (filename == null || filename.isBlank()) {
                    filename = UUID.randomUUID().toString();
                }

                if (sets.find(filename).isEmpty()) {
                    sets.save(filename, file.getInputStream());
                }
            }
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "images", consumes = { "multipart/form-data" })
    public ResponseEntity<Void> postImages(@RequestParam(name = "file") List<MultipartFile> files,
                                     JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String filename = file.getOriginalFilename();
                if (filename == null || filename.isBlank()) {
                    filename = UUID.randomUUID().toString();
                }

                if (sets.find(filename).isEmpty()) {
                    sets.saveImage(filename, file.getInputStream());
                }
            }
        }

        return ResponseEntity.noContent().build();
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
