package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.ComponentListItem;
import ch.fhnw.cemcloudbackend.repository.ComponentRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/components")
public class ComponentController {

    private final ComponentRepository components;
    private final InstallationRepository installations;

    public ComponentController(ComponentRepository components,
                               InstallationRepository installations) {
        this.components = components;
        this.installations = installations;
    }

    @GetMapping()
    public ResponseEntity<Iterable<ComponentListItem>> getAll(@RequestParam Optional<UUID> installationId) {
        Iterable<ch.fhnw.cemcloudbackend.entity.Component> components;
        if (installationId.isEmpty()) {
            components = this.components.findAll();
        } else {
            var optionalInstallation = installations.findById(installationId.get());

            if (optionalInstallation.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            components = optionalInstallation.get().getComponents();
        }

        return ResponseEntity.ok(StreamSupport.stream(components.spliterator(), false)
                .map(c -> new ComponentListItem(c.getId(), c.getName(), c.getType().getComponentType().getName()))
                .toList());
    }
}
