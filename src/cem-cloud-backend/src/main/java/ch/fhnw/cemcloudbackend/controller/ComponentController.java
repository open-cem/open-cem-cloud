package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.dto.*;
import ch.fhnw.cemcloudbackend.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
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
    private final ComponentParameterMetaRepository meta;
    private final SmartGridreadyRepository sgrDefinitions;

    public ComponentController(ComponentRepository components,
                               ComponentTypeRepository types,
                               InstallationRepository installations,
                               ManufacturerRepository manufacturers,
                               ComponentParameterMetaRepository meta,
                               SmartGridreadyRepository sgrDefinitions) {
        this.components = components;
        this.types = types;
        this.installations = installations;
        this.manufacturers = manufacturers;
        this.meta = meta;
        this.sgrDefinitions = sgrDefinitions;
    }

    @GetMapping()
    public ResponseEntity<Iterable<ComponentListItem>> getAll(@RequestParam Optional<UUID> installationId,
                                                              @RequestParam Optional<UUID> typeFamilyId) {
        Iterable<ch.fhnw.cemcloudbackend.entity.Component> components;
        if (installationId.isPresent() && typeFamilyId.isPresent()) {
            components = this.components.findAllByTypeComponentFamilyIdAndInstallationId(typeFamilyId.get(), installationId.get());
        } else if (installationId.isPresent()) {
            var optionalInstallation = installations.findById(installationId.get());

            if (optionalInstallation.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            components = optionalInstallation.get().getComponents();
        } else {
            components = this.components.findAll();
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

        Map<String, Object> parameter = component.getParameter() != null
                ? component.getParameter() : new HashMap<>();

        Component dto;
        if (component instanceof ch.fhnw.cemcloudbackend.entity.HardwareComponent hardwareComponent) {
            var manufacturer = hardwareComponent.getManufacturer() != null
                ? hardwareComponent.getManufacturer().getId()
                : null;
            var model = hardwareComponent.getModel() != null
                ? hardwareComponent.getModel().getId()
                : null;
            var channel = hardwareComponent.getCommunicationChannel() != null
                ? hardwareComponent.getCommunicationChannel().getId()
                : null;
            var smartGridreadyDefinition = hardwareComponent.getSmartGridreadyDefinition() != null
                ? hardwareComponent.getSmartGridreadyDefinition().getId()
                : null;

            dto = new Component(component.getId(), component.getName(), component.getType().getComponentType().getName(),
                    channel, manufacturer, model, smartGridreadyDefinition, parameter);
        } else {
            dto = new Component(component.getId(), component.getName(), component.getType().getComponentType().getName(),
                    parameter);
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("types")
    public ResponseEntity<Iterable<ComponentType>> getTypes() {
        Iterable<ch.fhnw.cemcloudbackend.entity.ComponentType> allTypes = types.findAll();

        return ResponseEntity.ok(StreamSupport.stream(allTypes.spliterator(), false)
                .map(type -> new ComponentType(type.getId(), type.getName(), type.getComponentType().getName()))
                .toList());
    }

    @GetMapping("{id}/communicationChannels")
    public ResponseEntity<Iterable<CommunicationChannelListItem>> getCommunicationChannels(@PathVariable UUID id) {
        Optional<ch.fhnw.cemcloudbackend.entity.Component> component = components.findById(id);
        if (component.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(component.get().getInstallation().getCommunicationChannels().stream()
                .map(channel -> new CommunicationChannelListItem(channel.getId(), channel.getName()))
                .toList());
    }

    @GetMapping("{id}/meta")
    public ResponseEntity<Iterable<ParameterMeta>> getMeta(@PathVariable UUID id) {
        Optional<ch.fhnw.cemcloudbackend.entity.Component> component = components.findById(id);
        if (component.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Iterable<ch.fhnw.cemcloudbackend.entity.ComponentParameterMeta> parameterMeta =
                meta.findAllByComponentType(component.get().getType());

        return ResponseEntity.ok(StreamSupport
                .stream(parameterMeta.spliterator(), false)
                .map(m -> new ParameterMeta(m.getName(), m.getLabel(), m.getType().toString(),
                        m.getListType() != null ? m.getListType().toString() : null,
                        m.getReferenceComponentFamily() != null ? m.getReferenceComponentFamily().getId() : null))
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
        installation.get().setOutOfSync(true);

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
        component.setParameter(request.parameter());
        component.getInstallation().setOutOfSync(true);

        if (component instanceof ch.fhnw.cemcloudbackend.entity.HardwareComponent hardwareComponent) {

            if (request.smartGridreadyDefinitionId() != null) {
                hardwareComponent.setManufacturer(null);
                hardwareComponent.setModel(null);

                Optional<ch.fhnw.cemcloudbackend.entity.SmartGridreadyDefinition> sgrDefinition = sgrDefinitions.findById(request.smartGridreadyDefinitionId());

                if (sgrDefinition.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }

                hardwareComponent.setSmartGridreadyDefinition(sgrDefinition.get());
            }
            else if (request.manufacturerId() != null) {
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
                hardwareComponent.setSmartGridreadyDefinition(null);
                hardwareComponent.setManufacturer(null);
                hardwareComponent.setModel(null);
            }

            if (request.channelId() != null) {
                Optional<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> channel = hardwareComponent.getInstallation().getCommunicationChannels()
                        .stream()
                        .filter(c -> c.getId().equals(request.channelId()))
                        .findFirst();

                if (channel.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                hardwareComponent.setCommunicationChannel(channel.get());
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

        ch.fhnw.cemcloudbackend.entity.Installation installation = component.get().getInstallation();
        installation.setOutOfSync(true);
        installations.save(installation);

        components.delete(component.get());

        return ResponseEntity.noContent().build();
    }
}
