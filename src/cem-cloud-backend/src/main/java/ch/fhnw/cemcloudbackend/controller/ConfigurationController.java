package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.entity.Component;
import ch.fhnw.cemcloudbackend.entity.ComponentFamily;
import ch.fhnw.cemcloudbackend.entity.Installation;
import ch.fhnw.cemcloudbackend.entity.InstallationCredentials;
import ch.fhnw.cemcloudbackend.model.configuration.CommunicationChannel;
import ch.fhnw.cemcloudbackend.model.configuration.Configuration;
import ch.fhnw.cemcloudbackend.model.configuration.Controller;
import ch.fhnw.cemcloudbackend.model.configuration.HardwareComponent;
import ch.fhnw.cemcloudbackend.repository.ComponentFamilyRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationCredentialsRepository;
import ch.fhnw.cemcloudbackend.repository.InstallationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.*;

@RestController
public class ConfigurationController {

    private final InstallationRepository installations;
    private final InstallationCredentialsRepository installationCredentials;
    private final ComponentFamilyRepository componentFamilies;

    public ConfigurationController(InstallationRepository installations, InstallationCredentialsRepository installationCredentials, ComponentFamilyRepository componentFamilies) {
        this.installations = installations;
        this.installationCredentials = installationCredentials;
        this.componentFamilies = componentFamilies;
    }

    @GetMapping("/installations/{installationNr}/configuration")
    public ResponseEntity<String> getConfiguration(@PathVariable String installationNr, @RequestParam String token) {
        Optional<InstallationCredentials> credentials = installationCredentials.findBySerialNumber(installationNr);

        if (credentials.isEmpty() || !BCrypt.checkpw(token, credentials.get().getBackendToken())) {
            return ResponseEntity.notFound().build();
        }

        Optional<Installation> installation = installations.findBySerialNumber(installationNr);

        if (installation.isPresent()) {
            installation.get().setOutOfSync(false);
            installations.save(installation.get());
        } else {
            return ResponseEntity.notFound().build();
        }

        Configuration configuration = buildConfiguration(installation.get());


        Yaml yaml = new Yaml(new OmitTypesRepresenter());
        String result = yaml.dumpAsMap(configuration);

        return ResponseEntity.ok(result);
    }

    public class OmitTypesRepresenter extends Representer {
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
                                                      Object propertyValue, Tag customTag) {

            if (UUID.class.equals(property.getType())) {
                return super
                        .representJavaBeanProperty(javaBean, property, propertyValue == null ? null : propertyValue.toString(), customTag);
            } else {
                return super
                        .representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        }

        protected Set<Property> getProperties(Class<? extends Object> type) {
            Set<Property> propertySet;
            if (typeDefinitions.containsKey(type)) {
                propertySet = typeDefinitions.get(type).getProperties();
            }else{
                propertySet =  getPropertyUtils().getProperties(type);
            }


            List<Property> propsList = new ArrayList<>(propertySet);
            propsList.sort(new BeanPropertyComparator());

            return new LinkedHashSet<>(propsList);
        }

        class BeanPropertyComparator implements Comparator<Property> {

            List<String> propertieNames = Arrays.asList(
                    "installationName", "creationTimestamp", "version", "communicationChannels", "sensors", "actuators", "devices", "controllers",
                    "id", "name", "manufacturer", "model", "type", "isSmartGridready", "smartGridreadyFileId", "communicationId", "isLogging",
                    "extra"
            );

            public int compare(Property p1, Property p2) {
                boolean p1InList = propertieNames.contains(p1.getName());
                boolean p2InList = propertieNames.contains(p2.getName());
                if (p1InList && !p2InList) {
                    return -1;
                } else if (!p1InList && p2InList) {
                    return 1;
                } else if (p1InList && p2InList) {
                    return propertieNames.indexOf(p1.getName()) - propertieNames.indexOf(p2.getName());
                } else {
                    return -1;
                }
            }
        }
    }

    private Configuration buildConfiguration(Installation installation) {
        List<CommunicationChannel> communicationsList = mapCommunicationChannels(installation.getCommunicationChannels());
        Set<Component> components = installation.getComponents();

        List<HardwareComponent> sensor = mapHardwareComponents(components, componentFamilies.findByName("SENSORS"));
        List<HardwareComponent> actuator = mapHardwareComponents(components, componentFamilies.findByName("ACTUATORS"));
        List<HardwareComponent> devices = mapHardwareComponents(components, componentFamilies.findByName("DEVICES"));
        List<Controller> controllers = mapControllers(components);

        return new Configuration(
                installation.getName(),
                1,
                communicationsList.toArray(new CommunicationChannel[0]),
                sensor.toArray(new HardwareComponent[0]),
                actuator.toArray(new HardwareComponent[0]),
                devices.toArray(new HardwareComponent[0]),
                controllers.toArray(new Controller[0])
        );
    }

    private List<Controller> mapControllers(Set<Component> components) {
        ComponentFamily componentFamily = componentFamilies.findByName("CONTROLLERS");
        return components.stream()
                .filter(component -> componentFamily.getId().equals(component.getType().getComponentType().getId()))
                .map(component -> new Controller(
                        component.getId(),
                        component.getType().getCode(),
                        component.getParameter()
                )).toList();
    }

    private List<HardwareComponent> mapHardwareComponents(Set<Component> components, ComponentFamily componentFamily) {
        return components.stream()
                .filter(component -> component instanceof ch.fhnw.cemcloudbackend.entity.HardwareComponent)
                .map(component -> (ch.fhnw.cemcloudbackend.entity.HardwareComponent) component)
                .filter(component -> componentFamily.getId().equals(component.getType().getComponentType().getId()))
                .map(component -> new HardwareComponent(
                        component.getId(),
                        component.getName(),
                        component.getManufacturer() != null ? component.getManufacturer().getName() : null,
                        component.getModel() != null ? component.getModel().getName() : null,
                        component.getType() != null ? component.getType().getCode() : null,
                        component.getSmartGridreadyDefinition() != null,
                        component.getSmartGridreadyDefinition() != null ? component.getSmartGridreadyDefinition().getId() : null,
                        component.getCommunicationChannel() != null ? component.getCommunicationChannel().getId() : null,
                        true,
                        component.getParameter()
                )).toList();
    }

    private List<CommunicationChannel> mapCommunicationChannels(Set<ch.fhnw.cemcloudbackend.entity.CommunicationChannel> communicationChannels) {
        return communicationChannels.stream().map(communicationChannel -> new CommunicationChannel(
                communicationChannel.getId(),
                communicationChannel.getName(),
                communicationChannel.getTyp().getCode(),
                communicationChannel.getParameter()
        )).toList();
    }


}
