package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/installations")
public class InstallationController {

    private final InstallationRepository installationRepository;

    public InstallationController(InstallationRepository installationRepository) {
        this.installationRepository = installationRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Installation>> getAll() {
        Iterable<Installation> all = installationRepository.findAll();

        return new ResponseEntity<>(all, HttpStatus.OK);
    }
}
