package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.*;
import ch.fhnw.cemcloudbackend.repository.ComponentRepository;
import ch.fhnw.cemcloudbackend.repository.ComponentTypeRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import ch.fhnw.cemcloudbackend.repository.ManufacturerRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("components")
public class ComponentController {

    private final ComponentRepository components;
    private final InstallationRepository installations;
    private final ComponentTypeRepository types;
    private final ManufacturerRepository manufacturers;

    public ComponentController(ComponentRepository components,
                               ComponentTypeRepository types,
                               InstallationRepository installations,
                               ManufacturerRepository manufacturers) {
        this.components = components;
        this.types = types;
        this.installations = installations;
        this.manufacturers = manufacturers;
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

    @GetMapping("{id}")
    public ResponseEntity<Component> get(@PathVariable UUID id) {
        Optional<ch.fhnw.cemcloudbackend.entity.Component> optionalComponent = components.findById(id);

        if (optionalComponent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ch.fhnw.cemcloudbackend.entity.Component component = optionalComponent.get();

        Component dto;
        if (component instanceof ch.fhnw.cemcloudbackend.entity.HardwareComponent hardwareComponent) {
            var manufacturer = hardwareComponent.getManufacturer() != null
                ? hardwareComponent.getManufacturer().getId()
                : null;
            var model = hardwareComponent.getModel() != null
                ? hardwareComponent.getModel().getId()
                : null;

            dto = new Component(component.getId(), component.getName(), component.getType().getComponentType().getName(),
                    manufacturer, model);
        } else {
            dto = new Component(component.getId(), component.getName(), component.getType().getComponentType().getName());
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/types")
    public ResponseEntity<Iterable<ComponentType>> getTypes() {
        Iterable<ch.fhnw.cemcloudbackend.entity.ComponentType> allTypes = types.findAll();

        return ResponseEntity.ok(StreamSupport.stream(allTypes.spliterator(), false)
                .map(type -> new ComponentType(type.getId(), type.getName(), type.getComponentType().getName()))
                .toList());
    }

    @PostMapping()
    @CrossOrigin(exposedHeaders = "Location")
    public ResponseEntity<Void> post(@Valid @RequestBody ComponentCreationRequest request) throws URISyntaxException {
        Optional<ch.fhnw.cemcloudbackend.entity.Installation> installation = installations.findById(request.installationId());
        if (installation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<ch.fhnw.cemcloudbackend.entity.ComponentType> type = types.findById((request.typeId()));
        if (type.isEmpty()) {
            return  ResponseEntity.notFound().build();
        }

        ch.fhnw.cemcloudbackend.entity.Component component;
        if (type.get().getComponentType().isHardwareComponent()) {
            component = new ch.fhnw.cemcloudbackend.entity.HardwareComponent();
        } else {
            component = new ch.fhnw.cemcloudbackend.entity.Component();
        }

        component.setName(String.format("%s (neu)", type.get().getName()));
        component.setType(type.get());
        component.setInstallation(installation.get());

        component = components.save(component);
        URI uri = new URI(String.format("/%s", component.getId()));

        return ResponseEntity.created(uri).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> put(@PathVariable UUID id, @RequestBody @Valid ComponentUpdateRequest request) {
        Optional<ch.fhnw.cemcloudbackend.entity.Component> optionalComponent = components.findById(id);

        if (optionalComponent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ch.fhnw.cemcloudbackend.entity.Component component = optionalComponent.get();
        component.setName(request.name());

        if (component instanceof ch.fhnw.cemcloudbackend.entity.HardwareComponent hardwareComponent) {

            if (request.manufacturerId() != null) {
                Optional<ch.fhnw.cemcloudbackend.entity.Manufacturer> manufacturer = manufacturers.findById(request.manufacturerId());
                if (manufacturer.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                hardwareComponent.setManufacturer(manufacturer.get());

                if (request.modelId() != null) {
                    Optional<ch.fhnw.cemcloudbackend.entity.Model> model = manufacturer.get().getModels()
                            .stream()
                            .filter(m -> m.getId().equals(request.modelId()))
                            .findFirst();

                    if (model.isEmpty()) {
                        return ResponseEntity.badRequest().build();
                    }
                    hardwareComponent.setModel(model.get());
                } else {
                    hardwareComponent.setModel(null);
                }
            } else {
                hardwareComponent.setManufacturer(null);
            }
        }

        components.save(component);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Optional<ch.fhnw.cemcloudbackend.entity.Component> component = components.findById(id);
        if (component.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        components.delete(component.get());

        return ResponseEntity.noContent().build();
    }
}
