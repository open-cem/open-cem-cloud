package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.SmartGridreadyDefinition;
import ch.fhnw.cemcloudbackend.repository.SmartGridreadyRepository;
import ch.fhnw.cemcloudbackend.repository.SmartGridReadyXmlRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("smartgridready")
public class SmartGridReadyController {
    private final SmartGridReadyXmlRepository smartGridReadyXmlRepository;
    private final SmartGridreadyRepository smartGridreadyRepository;

    public SmartGridReadyController(SmartGridReadyXmlRepository smartGridReadyXmlRepository,
                                    SmartGridreadyRepository smartGridreadyRepository) {
        this.smartGridReadyXmlRepository = smartGridReadyXmlRepository;
        this.smartGridreadyRepository = smartGridreadyRepository;
    }

    @GetMapping()
    public ResponseEntity<Iterable<SmartGridreadyDefinition>> getAll() {
        Iterable<ch.fhnw.cemcloudbackend.entity.SmartGridreadyDefinition> all = smartGridreadyRepository.findAll();

        return ResponseEntity.ok(StreamSupport.stream(all.spliterator(), false)
                .map(d -> new SmartGridreadyDefinition(d.getId(), d.getFileName()))
                .toList());
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable UUID id) throws IOException {

        Optional<byte[]> image = smartGridReadyXmlRepository.load(id);

        return image.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
