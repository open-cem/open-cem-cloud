package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/installations")
public class InstallationController {

    private final InstallationRepository installations;

    public InstallationController(InstallationRepository installations) {
        this.installations = installations;
    }

    @GetMapping
    public ResponseEntity<Iterable<Installation>> getAll() {
        Iterable<Installation> all = installations.findAll();

        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Installation> get(@PathVariable UUID id) {

        Optional<Installation> installation = installations.findById(id);

        return ResponseEntity.of(installation);
    }
}
