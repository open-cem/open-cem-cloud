package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.*;
import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.repository.CommunicationChannelParameterMetaRepository;
import ch.fhnw.cemcloudbackend.repository.CommunicationChannelRepository;
import ch.fhnw.cemcloudbackend.repository.CommunicationChannelTypeRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/communicationChannels")
public class CommunicationChannelController {

    private final CommunicationChannelRepository channels;
    private final CommunicationChannelTypeRepository types;
    private final CommunicationChannelParameterMetaRepository meta;
    private final InstallationRepository installations;

    public CommunicationChannelController(CommunicationChannelRepository channels,
                                          CommunicationChannelTypeRepository types,
                                          CommunicationChannelParameterMetaRepository meta,
                                          InstallationRepository installations) {
        this.channels = channels;
        this.types = types;
        this.meta = meta;
        this.installations = installations;
    }

    @GetMapping()
    public ResponseEntity<Iterable<CommunicationChannelListItem>> getAll(@RequestParam Optional<UUID> installationId) {
        Iterable<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> channels;
        if (installationId.isEmpty()) {
            channels = this.channels.findAll();
        } else {
            channels = this.channels.findAllByInstallationId(installationId.get());
        }

        return ResponseEntity.ok(StreamSupport
                .stream(channels.spliterator(), false)
                .map(channel -> new CommunicationChannelListItem(channel.getId(), channel.getName()))
                .toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<CommunicationChannel> get(@PathVariable UUID id) {
        Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> channel = channels.findById(id);

        if (channel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> parameter = channel.get().getParameter() != null
            ? channel.get().getParameter() : new HashMap<>();

        return ResponseEntity.ok(new CommunicationChannel(channel.get().getId(), channel.get().getName(), parameter));
    }

    @GetMapping("{id}/meta")
    public ResponseEntity<Iterable<CommunicationChannelParameterMeta>> getMeta(@PathVariable UUID id) {
        Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> channel = channels.findById(id);
        if (channel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Iterable<ch.fhnw.cemcloudbackend.entity.CommunicationChannelParameterMeta> parameterMeta =
                meta.findAllByCommunicationChannelTypeId(channel.get().getTyp().getId());

        return ResponseEntity.ok(StreamSupport
                .stream(parameterMeta.spliterator(), false)
                .map(m -> new CommunicationChannelParameterMeta(m.getName(), m.getLabel(), m.getType().toString(),
                        m.getListType() != null ? m.getListType().toString() : null))
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

    @PutMapping("{id}")
    public ResponseEntity<Void> put(@PathVariable UUID id, @Valid @RequestBody CommunicationChannelUpdateRequest request) {
        Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> optionalChannel = channels.findById(id);

        if (optionalChannel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ch.fhnw.cemcloudbackend.entity.CommunicationChannel channel = optionalChannel.get();
        channel.setName(request.name());
        channel.setParameter(request.parameters());
        channels.save(channel);

        return ResponseEntity.noContent().build();
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
