package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.InstallationCreateRequest;
import ch.fhnw.cemcloudbackend.entity.InstallationCredentials;
import ch.fhnw.cemcloudbackend.dto.InstallationListItem;
import ch.fhnw.cemcloudbackend.dto.InstallationUpdateRequest;
import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.entity.MqttAccessControl;
import ch.fhnw.cemcloudbackend.mqtt.Mqtt;
import ch.fhnw.cemcloudbackend.repository.InstallationCredentialsRepository;
import ch.fhnw.cemcloudbackend.entity.InstallationAccess;
import ch.fhnw.cemcloudbackend.model.User;
import ch.fhnw.cemcloudbackend.repository.InstallationAccessRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationImageRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import ch.fhnw.cemcloudbackend.repository.MqttAccessControlRepository;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

@RestController
@RequestMapping("/installations")
public class InstallationController extends BaseController {

    private final InstallationRepository installations;
    private final InstallationCredentialsRepository installationCredentials;
    private final InstallationImageRepository images;
    private final MqttAccessControlRepository mqttAccessControlRepository;
    private final Mqtt mqtt;
    private final InstallationAccessRepository accesses;

    public InstallationController(InstallationRepository installations,
                                  InstallationCredentialsRepository installationCredentials,
                                  InstallationImageRepository images,
                                  MqttAccessControlRepository mqttAccessControlRepository,
                                  Mqtt mqtt,
                                  InstallationAccessRepository accesses) {
        this.installations = installations;
        this.installationCredentials = installationCredentials;
        this.images = images;
        this.mqttAccessControlRepository = mqttAccessControlRepository;
        this.mqtt = mqtt;
        this.accesses = accesses;
    }

    @GetMapping
    public ResponseEntity<Iterable<InstallationListItem>> getAll(JwtAuthenticationToken auth) {
        User user = getUser(auth);

        acceptPendingInvites(user);

        Iterable<Installation> all = installations.getInstallations(user);

        Iterable<InstallationListItem> result = StreamSupport.stream(all.spliterator(), false)
                .map(installation -> new InstallationListItem(installation.getId(),
                        installation.getName(), installation.getSerialNumber(),
                        getInstallationImageUrl(installation)))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("{id}")
    public ResponseEntity<ch.fhnw.cemcloudbackend.dto.Installation> get(@PathVariable UUID id, JwtAuthenticationToken auth) {
        User user = getUser(auth);

        Optional<Installation> installation = installations.getInstallation(id, user);

        if (installation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ch.fhnw.cemcloudbackend.dto.Installation(installation.get().getId(),
                installation.get().getName(), installation.get().getSerialNumber(),
                getInstallationImageUrl(installation.get()), installation.get().isOutOfSync()));
    }

    @GetMapping(value = "{id}/image", produces = MediaType.IMAGE_JPEG_VALUE + ";" + MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable UUID id, JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        Optional<Installation> optionalInstallation = installations.getInstallation(id, user);

        if (optionalInstallation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<byte[]> image = images.load(optionalInstallation.get());

        return image.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    @CrossOrigin(exposedHeaders = "Location")
    public ResponseEntity<Void> post(@Valid @RequestBody InstallationCreateRequest request, JwtAuthenticationToken auth)
            throws URISyntaxException {
        User user = getUser(auth);

        Installation installation = new Installation();
        installation.setId(UUID.randomUUID());
        installation.setName(request.name());
        installation.setSerialNumber(request.serialNumber());
        installation.setOutOfSync(true);

        installation = installations.save(installation);

        InstallationAccess access = new InstallationAccess();
        access.setInstallation(installation);
        access.setUserId(user.getId());
        access.setUserEmail(user.getEmail());
        accesses.save(access);

        URI uri = new URI(format("/%s", installation.getId()));

        return ResponseEntity.created(uri).build();
    }

    @PostMapping("{id}/invite")
    public ResponseEntity<Void> invite(@PathVariable UUID id, @RequestParam("email") String email) {
        if (email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Installation> installation = installations.findById(id);
        if (installation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (accesses.findByInstallationIdAndUserEmail(id, email).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        InstallationAccess access = new InstallationAccess();
        access.setInstallation(installation.get());
        access.setUserEmail(email);
        accesses.save(access);

        return ResponseEntity.ok().build();
    }

    @PostMapping("{id}/sync")
    public ResponseEntity<Void> sync(@PathVariable UUID id) {
        Optional<Installation> installation = installations.findById(id);

        if (installation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        sendNewConfigurationEvent(installation.get().getSerialNumber());

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<Void> put(@PathVariable UUID id,
                                    @Valid @RequestPart("installation") InstallationUpdateRequest request,
                                    @RequestPart(value = "image", required = false) MultipartFile image,
                                    JwtAuthenticationToken auth) throws IOException {
        User user = getUser(auth);

        Optional<Installation> optionalInstallation = installations.getInstallation(id, user);

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

    @PostMapping("prepare")
    public ResponseEntity<ch.fhnw.cemcloudbackend.dto.InstallationCredential> prepare(@RequestParam Optional<String> requestedSerialNumber) {
        String serialNumber;
        if (requestedSerialNumber.isEmpty()) {
            serialNumber = UUID.randomUUID().toString();
        } else {
            serialNumber = requestedSerialNumber.get();
        }

        Optional<InstallationCredentials> bySerialNumber = installationCredentials.findBySerialNumber(serialNumber);
        if (bySerialNumber.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        ch.fhnw.cemcloudbackend.dto.InstallationCredential credential = new ch.fhnw.cemcloudbackend.dto.InstallationCredential(
                serialNumber, generatePassword(60), generatePassword(60)
        );

        InstallationCredentials installationCredential = new InstallationCredentials();

        installationCredential.setSerialNumber(credential.serialNumber());
        installationCredential.setBackendToken(BCrypt.hashpw(credential.backendToken(), BCrypt.gensalt()));
        installationCredential.setMqttPassword(BCrypt.hashpw(credential.mqttPassword(), BCrypt.gensalt()));

        installationCredentials.save(installationCredential);

        // Create access control for installation

        MqttAccessControl subscribeAccess = new MqttAccessControl();
        subscribeAccess.setInstallationCredential(installationCredential);
        subscribeAccess.setAccessLevel(MqttAccessControl.AccessLevel.SUBSCRIBE);
        subscribeAccess.setTopic(format("installations/%s", serialNumber));

        mqttAccessControlRepository.save(subscribeAccess);

        MqttAccessControl readAccess = new MqttAccessControl();
        readAccess.setInstallationCredential(installationCredential);
        readAccess.setAccessLevel(MqttAccessControl.AccessLevel.READ);
        readAccess.setTopic(format("installations/%s", serialNumber));

        mqttAccessControlRepository.save(readAccess);

        return ResponseEntity.ok(credential);
    }

    private String generatePassword(int length) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz@!#$*";
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, length)
                .mapToObj(i -> String.valueOf(chars.charAt(random.nextInt(chars.length()))))
                .reduce("", String::concat);
    }

    private void acceptPendingInvites(User user) {
        Iterable<InstallationAccess> invites = accesses.findAllByUserEmailAndUserIdIsNull(user.getEmail());
        for (InstallationAccess invite : invites) {
            invite.setUserId(user.getId());
            accesses.save(invite);
        }
    }

    private static String getInstallationImageUrl(Installation installation) {
        return format("%s/image", installation.getId());
    }

    private void sendNewConfigurationEvent(String serialNumber) {
        mqtt.sendMessage(format("installations/%s", serialNumber), "{ \"event\": \"newConfiguration\" }");
    }
}
