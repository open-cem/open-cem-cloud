package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.InstallationCreateRequest;
import ch.fhnw.cemcloudbackend.dto.InstallationUpdateRequest;
import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
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

    @PostMapping
    @CrossOrigin(exposedHeaders = "Location")
    public ResponseEntity<Void> post(@Valid @RequestBody InstallationCreateRequest request) throws URISyntaxException {

        Installation installation = new Installation();
        installation.setId(UUID.randomUUID());
        installation.setName(request.name());
        installation.setSerialNumber(request.serialNumber());

        installation = installations.save(installation);
        URI uri = new URI(String.format("/%s", installation.getId()));

        return ResponseEntity.created(uri).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> put(@PathVariable UUID id, @Valid @RequestBody InstallationUpdateRequest request) {
        Optional<Installation> optionalInstallation = installations.findById(id);
        if (optionalInstallation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Installation installation = optionalInstallation.get();
        installation.setName(request.name());
        installation.setSerialNumber(request.serialNumber());
        installations.save(installation);

        return ResponseEntity.ok().build();
    }
}
