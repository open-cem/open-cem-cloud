package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.SmartGridreadyDefinition;
import ch.fhnw.cemcloudbackend.model.Role;
import ch.fhnw.cemcloudbackend.model.User;
import ch.fhnw.cemcloudbackend.repository.SmartGridReadyXmlRepository;
import ch.fhnw.cemcloudbackend.repository.SmartGridreadyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("smartgridready")
public class SmartGridReadyController extends BaseController {

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

        Optional<ch.fhnw.cemcloudbackend.entity.SmartGridreadyDefinition> definition = smartGridreadyRepository.findById(id);

        if (definition.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<byte[]> image = smartGridReadyXmlRepository.load(definition.get().getFileName());

        return image.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
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
                UUID id = UUID.randomUUID();
                if (filename == null || filename.isBlank()) {
                    filename = id.toString();
                }

                if (smartGridreadyRepository.findByFileName(filename).isEmpty()) {
                    smartGridReadyXmlRepository.save(filename, file.getInputStream());
                    var smartGridreadyDefinition = new ch.fhnw.cemcloudbackend.entity.SmartGridreadyDefinition();
                    smartGridreadyDefinition.setId(id);
                    smartGridreadyDefinition.setFileName(filename);
                    smartGridreadyRepository.save(smartGridreadyDefinition);
                }
            }
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        if (!user.isInRole(Role.ADMINISTRATOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<ch.fhnw.cemcloudbackend.entity.SmartGridreadyDefinition> sgrDefinition = smartGridreadyRepository.findById(id);
        if (sgrDefinition.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        smartGridReadyXmlRepository.delete(sgrDefinition.get().getFileName());
        smartGridreadyRepository.delete(sgrDefinition.get());

        return ResponseEntity.noContent().build();
    }
}
