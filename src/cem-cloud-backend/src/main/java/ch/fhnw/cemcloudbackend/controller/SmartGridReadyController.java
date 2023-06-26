package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.repository.SmartGridReadyRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/smartgridready")
public class SmartGridReadyController {
    private final SmartGridReadyRepository smartGridReadyRepository;

    public SmartGridReadyController(SmartGridReadyRepository smartGridReadyRepository) {
        this.smartGridReadyRepository = smartGridReadyRepository;
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable UUID id) throws IOException {

        Optional<byte[]> image = smartGridReadyRepository.load(id);

        return image.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
