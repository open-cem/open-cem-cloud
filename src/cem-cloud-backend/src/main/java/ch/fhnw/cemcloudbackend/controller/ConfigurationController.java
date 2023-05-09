package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.model.configuration.Communication;
import ch.fhnw.cemcloudbackend.model.configuration.Configuration;
import ch.fhnw.cemcloudbackend.model.configuration.Controller;
import ch.fhnw.cemcloudbackend.model.configuration.HardwareComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController()
public class ConfigurationController {

    @GetMapping("/configuration")
    public String GetConfiguration() {
        // returns hardcoded configuration for now.
        final String installationName = "EFH Test";
        final int version = 1;

        final Communication[] communications = new Communication[2];
        UUID com1Id = UUID.randomUUID();
        Map<String, Object> com1Extra = new HashMap<>();
        com1Extra.put("baudrate", 19200);
        com1Extra.put("port", "COM5");
        com1Extra.put("parity", "EVEN");
        communications[0] = new Communication(com1Id, "Modbus RTU", "MODBUS_RTU", com1Extra);
        Map<String, Object> com2Extra = new HashMap<>();
        com2Extra.put("server_address", "https://shelly-54-eu.shelly.cloud/");
        com2Extra.put("auth_key", "MTUyNjU5dWlk6D393AB193944CE2B1D84E0B573EAB1271DA6F2AF2BC54F67779F5BC27C31E90AD7C7075E0F813D8");
        communications[1] = new Communication(UUID.randomUUID(), "Shelly Cloud", "SHELLY_CLOUD", com2Extra);

        final HardwareComponent[] sensors = new HardwareComponent[1];
        sensors[0] = new HardwareComponent(UUID.randomUUID(), "Zaehler Nummer 1", "ABB", "ABB B23 112-100", "POWER_SENSOR", true, UUID.randomUUID(), com1Id);
        Map<String, Object> extra = new HashMap<>();
        extra.put("has_energy_export", false);
        extra.put("address", "192.168.0.11");
        extra.put("max_power", 10);
        sensors[0].setExtra(extra);

        Controller[] controllers = new Controller[3];
        Map<String, Object> c1Extras = new HashMap<>();
        c1Extras.put("limits", List.of(0, 1, 2.3, 5));
        controllers[0] = new Controller(UUID.randomUUID(), "Some Controller", "STEPWISE_EXCESS_CONTROLLER", c1Extras);
        Map<String, Object> c2Extras = new HashMap<>();
        c2Extras.put("min_state", 19);
        c2Extras.put("max_state", 23);
        c2Extras.put("solar_tarif", 35.3);
        c2Extras.put("grid_tarif", 60.5);
        controllers[1] = new Controller(UUID.randomUUID(), "My Price Controller", "PRICE_CONTROLLER", c2Extras);
        Map<String, Object> c3Extras = new HashMap<>();
        c3Extras.put("min_temp", 20);
        c3Extras.put("max_temp", 25);
        c3Extras.put("sensor_combination_mode", "average");
        c3Extras.put("sensor_ids", List.of("0f7ab01f-8579-4aa8-91e9-bf04e144ef58", "c167b516-4c0f-45b9-917e-b73909b719ed"));
        controllers[2] = new Controller(UUID.randomUUID(), "Wärmepumpe Controller", "HEAT_PUMP_CONTROLLER", c3Extras);

        Configuration configuration = new Configuration(installationName, version, communications, sensors, new HardwareComponent[0], new HardwareComponent[0], controllers);

        Yaml yaml = new Yaml(new OmitTypesRepresenter());
        String result = yaml.dumpAsMap(configuration);

        return result;
    }

    public class OmitTypesRepresenter extends Representer {
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
                                                      Object propertyValue, Tag customTag) {
            if (UUID.class.equals(property.getType())) {
                return super
                        .representJavaBeanProperty(javaBean, property, propertyValue.toString(), customTag);
            } else {
                return super
                        .representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        }
    }
}
