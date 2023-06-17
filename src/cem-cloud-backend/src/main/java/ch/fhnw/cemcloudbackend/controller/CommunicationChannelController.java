package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.CommunicationChannel;
import ch.fhnw.cemcloudbackend.dto.CommunicationChannelTypCreationRequest;
import ch.fhnw.cemcloudbackend.dto.CommunicationChannelType;
import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.repository.CommunicationChannelRepository;
import ch.fhnw.cemcloudbackend.repository.CommunicationChannelTypeRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/communicationChannels")
public class CommunicationChannelController {

    private final CommunicationChannelRepository channels;
    private final CommunicationChannelTypeRepository types;
    private final InstallationRepository installations;

    public CommunicationChannelController(CommunicationChannelRepository channels,
                                          CommunicationChannelTypeRepository types,
                                          InstallationRepository installations) {
        this.channels = channels;
        this.types = types;
        this.installations = installations;
    }

    @GetMapping()
    public ResponseEntity<Iterable<CommunicationChannel>> getAll(@RequestParam Optional<UUID> installationId) {
        Iterable<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> channels;
        if (installationId.isEmpty()) {
            channels = this.channels.findAll();
        } else {
            channels = this.channels.findAllByInstallationId(installationId.get());
        }

        return ResponseEntity.ok(StreamSupport
                .stream(channels.spliterator(), false)
                .map(channel -> new CommunicationChannel(channel.getId(), channel.getName()))
                .toList());
    }

    @GetMapping("/types")
    public ResponseEntity<Iterable<CommunicationChannelType>> getTypes() {
        Iterable<ch.fhnw.cemcloudbackend.entity.CommunicationChannelType> types = this.types.findAll();

        return ResponseEntity.ok(StreamSupport
                .stream(types.spliterator(), false)
                .map(type -> new CommunicationChannelType(type.getId(), type.getName()))
                .toList());
    }

    @PostMapping()
    @CrossOrigin(exposedHeaders = "Location")
    public ResponseEntity<Void> post(@Valid @RequestBody CommunicationChannelTypCreationRequest request) throws URISyntaxException {
        Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannelType> type = types.findById(request.typeId());
        if (type.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Installation> installation = installations.findById(request.installationId());
        if (installation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var channel = new ch.fhnw.cemcloudbackend.entity.CommunicationChannel();
        channel.setTyp(type.get());
        channel.setName(String.format("Neuer %s Kommunikationskanal", type.get().getName()));
        channel.setInstallation(installation.get());

        channel = channels.save(channel);
        URI uri = new URI(String.format("/%s", channel.getId()));

        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> channel = channels.findById(id);

        if (channel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        channels.delete(channel.get());

        return ResponseEntity.noContent().build();
    }
}
