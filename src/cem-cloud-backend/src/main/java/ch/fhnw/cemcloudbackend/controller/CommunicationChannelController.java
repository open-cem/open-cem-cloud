package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.*;
import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.model.User;
import ch.fhnw.cemcloudbackend.repository.CommunicationChannelParameterMetaRepository;
import ch.fhnw.cemcloudbackend.repository.CommunicationChannelRepository;
import ch.fhnw.cemcloudbackend.repository.CommunicationChannelTypeRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/communicationChannels")
public class CommunicationChannelController extends BaseController {

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
    public ResponseEntity<Iterable<CommunicationChannelListItem>> getAll(@RequestParam UUID installationId,
                                                                         JwtAuthenticationToken auth) {
        User user = getUser(auth);
        var installation = installations.getInstallation(installationId, user);
        if (installation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Iterable<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> channels = installation.get().getCommunicationChannels();

        return ResponseEntity.ok(StreamSupport
                .stream(channels.spliterator(), false)
                .map(channel -> new CommunicationChannelListItem(channel.getId(), channel.getName()))
                .toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<CommunicationChannel> get(@PathVariable UUID id, JwtAuthenticationToken aut) {
        User user = getUser(aut);
        Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> channel = channels.getChannel(id, user);

        if (channel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> parameter = channel.get().getParameter() != null
            ? channel.get().getParameter() : new HashMap<>();

        return ResponseEntity.ok(new CommunicationChannel(channel.get().getId(), channel.get().getName(), parameter));
    }

    @GetMapping("{id}/meta")
    public ResponseEntity<Iterable<ParameterMeta>> getMeta(@PathVariable UUID id, JwtAuthenticationToken auth) {
        User user = getUser(auth);
        Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> channel = channels.getChannel(id, user);
        if (channel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Iterable<ch.fhnw.cemcloudbackend.entity.CommunicationChannelParameterMeta> parameterMeta =
                meta.findAllByCommunicationChannelTypeId(channel.get().getTyp().getId());

        return ResponseEntity.ok(StreamSupport
                .stream(parameterMeta.spliterator(), false)
                .map(m -> new ParameterMeta(m.getName(), m.getLabel(), m.getType().toString(),
                        m.getListType() != null ? m.getListType().toString() : null, null))
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
    public ResponseEntity<Void> post(@Valid @RequestBody CommunicationChannelTypCreationRequest request,
                                     JwtAuthenticationToken auth) throws URISyntaxException {
        User user = getUser(auth);
        Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannelType> type = types.findById(request.typeId());
        if (type.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Installation> installation = installations.getInstallation(request.installationId(), user);
        if (installation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var channel = new ch.fhnw.cemcloudbackend.entity.CommunicationChannel();
        channel.setTyp(type.get());
        channel.setName(String.format("Neuer %s Kommunikationskanal", type.get().getName()));
        channel.setInstallation(installation.get());
        installation.get().setOutOfSync(true);

        channel = channels.save(channel);
        URI uri = new URI(String.format("/%s", channel.getId()));

        return ResponseEntity.created(uri).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> put(@PathVariable UUID id,
                                    @Valid @RequestBody CommunicationChannelUpdateRequest request,
                                    JwtAuthenticationToken auth) {
        User user = getUser(auth);
        Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> optionalChannel = channels.getChannel(id, user);

        if (optionalChannel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ch.fhnw.cemcloudbackend.entity.CommunicationChannel channel = optionalChannel.get();
        channel.setName(request.name());
        channel.setParameter(request.parameter());
        channel.getInstallation().setOutOfSync(true);
        channels.save(channel);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, JwtAuthenticationToken auth) {
        User user = getUser(auth);
        Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> channel = channels.getChannel(id, user);

        if (channel.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Installation installation = channel.get().getInstallation();
        installation.setOutOfSync(true);
        installations.save(installation);

        channels.delete(channel.get());

        return ResponseEntity.noContent().build();
    }
}
