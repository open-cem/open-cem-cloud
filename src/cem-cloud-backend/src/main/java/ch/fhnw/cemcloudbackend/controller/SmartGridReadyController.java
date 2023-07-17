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

import java.io.IOException;
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

        Optional<byte[]> image = smartGridReadyXmlRepository.load(id);

        return image.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
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
