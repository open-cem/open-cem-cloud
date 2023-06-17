package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.CommunicationChannel;
import ch.fhnw.cemcloudbackend.repository.CommunicationChannelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/communicationChannels")
public class CommunicationChannelController {

    private final CommunicationChannelRepository channels;

    public CommunicationChannelController(CommunicationChannelRepository channels) {
        this.channels = channels;
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
}
