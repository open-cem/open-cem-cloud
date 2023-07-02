package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.InstallationCreateRequest;
import ch.fhnw.cemcloudbackend.dto.InstallationListItem;
import ch.fhnw.cemcloudbackend.dto.InstallationUpdateRequest;
import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.repository.InstallationImageRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/installations")
public class InstallationController {

    private final InstallationRepository installations;
    private final InstallationImageRepository images;

    public InstallationController(InstallationRepository installations, InstallationImageRepository images) {
        this.installations = installations;
        this.images = images;
    }

    @GetMapping
    public ResponseEntity<Iterable<InstallationListItem>> getAll() {
        Iterable<Installation> all = installations.findAll();

        Iterable<InstallationListItem> result = StreamSupport.stream(all.spliterator(), false)
                .map(installation -> new InstallationListItem(installation.getId(),
                        installation.getName(), installation.getSerialNumber(),
                        getInstallationImageUrl(installation)))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("{id}")
    public ResponseEntity<ch.fhnw.cemcloudbackend.dto.Installation> get(@PathVariable UUID id) {

        Optional<Installation> installation = installations.findById(id);

        if (installation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ch.fhnw.cemcloudbackend.dto.Installation(installation.get().getId(),
                installation.get().getName(), installation.get().getSerialNumber(),
                getInstallationImageUrl(installation.get()), installation.get().isOutOfSync()));
    }

    @GetMapping(value = "{id}/image", produces = MediaType.IMAGE_JPEG_VALUE + ";" + MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable UUID id) throws IOException {

        Optional<Installation> optionalInstallation = installations.findById(id);
        if (optionalInstallation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<byte[]> image = images.load(optionalInstallation.get());

        return image.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    @CrossOrigin(exposedHeaders = "Location")
    public ResponseEntity<Void> post(@Valid @RequestBody InstallationCreateRequest request) throws URISyntaxException {

        Installation installation = new Installation();
        installation.setId(UUID.randomUUID());
        installation.setName(request.name());
        installation.setSerialNumber(request.serialNumber());
        installation.setOutOfSync(true);

        installation = installations.save(installation);
        URI uri = new URI(String.format("/%s", installation.getId()));

        return ResponseEntity.created(uri).build();
    }

    @PostMapping("{id}/sync")
    public ResponseEntity<Void> sync(@PathVariable UUID id) {
        Optional<Installation> installation = installations.findById(id);

        if (installation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        //TODO: execute MQTT sync

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<Void> put(@PathVariable UUID id, @Valid @RequestPart("installation") InstallationUpdateRequest request,
                                    @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        Optional<Installation> optionalInstallation = installations.findById(id);
        if (optionalInstallation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Installation installation = optionalInstallation.get();
        installation.setName(request.name());
        installation.setSerialNumber(request.serialNumber());
        installation.setOutOfSync(true);

        if (image != null && !image.isEmpty()) {
            String filename = image.getOriginalFilename();
            if (filename == null || filename.isBlank()) {
                filename = UUID.randomUUID().toString();
            }

            images.save(installation, filename, image.getInputStream());
            installation.setFilename(filename);
        }

        installations.save(installation);

        return ResponseEntity.noContent().build();
    }

    private static String getInstallationImageUrl(Installation installation) {
        return String.format("%s/image", installation.getId());
    }
}
