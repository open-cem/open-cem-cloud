package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.model.configuration.CommunicationChannel;
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

import java.util.*;

@RestController()
public class ConfigurationController {

    @GetMapping("/installations/{installationnr}/configuration")
    public String getConfiguration() {
        // returns hardcoded configuration for now.
        final String installationName = "EFH Test";
        final int version = 1;

        List<CommunicationChannel> communicationsList = new ArrayList<>();

        CommunicationChannel modbus_rtu = new CommunicationChannel(UUID.randomUUID(), "Modbus RTU", "MODBUS_RTU", new HashMap<>());
        modbus_rtu.getExtra().put("baudrate", 19200);
        modbus_rtu.getExtra().put("port", "COM5");
        modbus_rtu.getExtra().put("parity", "EVEN");

        communicationsList.add(modbus_rtu);

        CommunicationChannel modbus_tcp = new CommunicationChannel(UUID.randomUUID(), "Modbus TCP", "MODBUS_TCP", new HashMap<>());
        modbus_tcp.getExtra().put("address", "192.168.0.8");
        modbus_tcp.getExtra().put("port", 502);

        communicationsList.add(modbus_tcp);

        CommunicationChannel shelly_cloud = new CommunicationChannel(UUID.randomUUID(), "Shelly Cloud", "SHELLY_CLOUD", new HashMap<>());
        shelly_cloud.getExtra().put("serverAddress", "https://shelly-54-eu.shelly.cloud/");
        shelly_cloud.getExtra().put("authKey", "MTUyNjU5dWlk6D393AB193944CE2B1D84E0B573EAB1271DA6F2AF2BC54F67779F5BC27C31E90AD7C7075E0F813D8");

        communicationsList.add(shelly_cloud);

        CommunicationChannel shelly_local = new CommunicationChannel(UUID.randomUUID(), "Shelly Local", "SHELLY_LOCAL", null);

        communicationsList.add(shelly_local);


        List<HardwareComponent> sensorList = new ArrayList<>();

        HardwareComponent powerSensor1 = new HardwareComponent(UUID.randomUUID(),
                "Zaehler Nummer 1",
                "ABB",
                "ABB B23 112-100",
                "POWER_SENSOR",
                true,
                UUID.randomUUID(),
                modbus_rtu.getId(),
                true);
        powerSensor1.setExtra(new HashMap<>());

        powerSensor1.getExtra().put("hasEnergyExport", false);
        powerSensor1.getExtra().put("address", "1");
        powerSensor1.getExtra().put("maxPower", 10);

        sensorList.add(powerSensor1);


        HardwareComponent powerSensor2 = new HardwareComponent(UUID.randomUUID(),
                "Zaehler Nummer 2 bidirektional",
                "ABB",
                "ABB B23 312-100",
                "POWER_SENSOR",
                false,
                null,
                modbus_rtu.getId(),
                true);
        powerSensor2.setExtra(new HashMap<>());

        powerSensor2.getExtra().put("hasEnergyExport", true);
        powerSensor2.getExtra().put("address", "2");
        powerSensor2.getExtra().put("maxPower", 10);

        sensorList.add(powerSensor2);

        HardwareComponent shellyPowerSensor = new HardwareComponent(UUID.randomUUID(),
                "Zaehler Nummer 3 bidirektional",
                "Shelly",
                "3EM",
                "POWER_SENSOR",
                false,
                null,
                shelly_local.getId(),
                true);
        shellyPowerSensor.setExtra(new HashMap<>());

        shellyPowerSensor.getExtra().put("hasEnergyExport", true);
        shellyPowerSensor.getExtra().put("address", "192.168.1.113");
        shellyPowerSensor.getExtra().put("maxPower", 10);

        sensorList.add(shellyPowerSensor);

        HardwareComponent tempSensor = new HardwareComponent(
                UUID.randomUUID(),
                "Wohnzimmer Temperatursensor",
                "Shelly",
                "HT",
                "TEMPERATURE_SENSOR_ROOM",
                false,
                null,
                shelly_cloud.getId(),
                true
        );
        tempSensor.setExtra(new HashMap<>());

        tempSensor.getExtra().put("address", "701f93");
        tempSensor.getExtra().put("maxTemp", 50);
        tempSensor.getExtra().put("minTemp", -10);

        sensorList.add(tempSensor);

        List<HardwareComponent> actuatorList = new ArrayList<>();

        HardwareComponent shellyRelais = new HardwareComponent(
                UUID.randomUUID(),
                "Erstes Relais",
                "Shelly",
                "PRO_2PM",
                "RELAIS",
                false,
                null,
                shelly_local.getId(),
                true
        );
        shellyRelais.setExtra(new HashMap<>());

        shellyRelais.getExtra().put("address", "192.168.1.112");
        shellyRelais.getExtra().put("nChannels", 2);

        actuatorList.add(shellyRelais);

        HardwareComponent shellyRelais2 = new HardwareComponent(
                UUID.randomUUID(),
                "Zweites Relais",
                "Shelly",
                "PRO_4PM",
                "RELAIS",
                false,
                null,
                shelly_local.getId(),
                true
        );
        shellyRelais2.setExtra(new HashMap<>());

        shellyRelais2.getExtra().put("address", "192.168.1.114");
        shellyRelais2.getExtra().put("nChannels", 4);

        actuatorList.add(shellyRelais2);

        List<Controller> controllerList = new ArrayList<>();

        Controller excessController = new Controller(
                UUID.randomUUID(),
                "EXCESS_CONTROLLER",
                new HashMap<>()
        );

        excessController.getExtra().put("limit", 2);

        controllerList.add(excessController);

        Controller stepwiseController = new Controller(
                UUID.randomUUID(),
                "STEPWISE_EXCESS_CONTROLLER",
                new HashMap<>()
        );

        stepwiseController.getExtra().put("limits", List.of(
                0, 1, 2.3, 5
        ));

        controllerList.add(stepwiseController);

        Controller coverageController = new Controller(
                UUID.randomUUID(),
                "COVERAGE_CONTROLLER",
                new HashMap<>()
        );

        coverageController.getExtra().put("limit", 1.3);

        controllerList.add(coverageController);

        Controller dynamicExcessController = new Controller(
                UUID.randomUUID(),
                "DYNAMIC_EXCESS_CONTROLLER",
                new HashMap<>()
        );

        dynamicExcessController.getExtra().put("limitMin", 4);
        dynamicExcessController.getExtra().put("limitMax", 8);

        controllerList.add(dynamicExcessController);

        Controller priceController = new Controller(
                UUID.randomUUID(),
                "PRICE_CONTROLLER",
                new HashMap<>()
        );

        priceController.getExtra().put("minState", 19);
        priceController.getExtra().put("maxState", 23);
        priceController.getExtra().put("solarTarif", 35.3);
        priceController.getExtra().put("gridTarif", 60.5);

        controllerList.add(priceController);




        List<HardwareComponent> devices = new ArrayList<>();

        HardwareComponent pvAnlage = new HardwareComponent(
                UUID.randomUUID(),
                "PV-Anlage West",
                "Musterfabrik",
                "PV Mustermodel",
                "PV_PLANT",
                false,
                null,
                null,
                true
        );
        pvAnlage.setExtra(new HashMap<>());

        pvAnlage.getExtra().put("isSimulated", false);
        pvAnlage.getExtra().put("maxPower", 5);
        pvAnlage.getExtra().put("idPowerSensor", powerSensor1.getId().toString());

        devices.add(pvAnlage);

        HardwareComponent centralPower = new HardwareComponent(
                UUID.randomUUID(),
                "Hausanschluss Power Sensor",
                null,
                null,
                "CENTRAL_POWER_METER",
                false,
                null,
                null,
                true
        );
        centralPower.setExtra(new HashMap<>());

        centralPower.getExtra().put("idPowerSensor", powerSensor2.getId().toString());
        centralPower.getExtra().put("maxPower", 10);

        devices.add(centralPower);


        HardwareComponent heatPump = new HardwareComponent(
                UUID.randomUUID(),
                "Meine Waermepumpe",
                "Musterfabrik",
                null,
                "HEAT_PUMP",
                false,
                null,
                null,
                true
        );
        heatPump.setExtra(new HashMap<>());

        heatPump.getExtra().put("idRelais", shellyRelais.getId().toString());
        heatPump.getExtra().put("idPowerSensor", shellyPowerSensor.getId().toString());
        heatPump.getExtra().put("nominalPower", 5);
        heatPump.getExtra().put("isSimulated", false);
        heatPump.getExtra().put("channels", List.of(0, 1));
        heatPump.getExtra().put("channelConfig", List.of(
                "11", "10", "01", "00"
        ));
        heatPump.getExtra().put("idTempSensorRoom", tempSensor.getId().toString());
        heatPump.getExtra().put("idController", stepwiseController.getId().toString());

        devices.add(heatPump);

        HardwareComponent evCharger = new HardwareComponent(
                UUID.randomUUID(),
                "Meine Ladestation",
                "Musterfabrik",
                null,
                "EV_CHARGER",
                false,
                null,
                null,
                true
        );
        evCharger.setExtra(new HashMap<>());

        evCharger.getExtra().put("idController", modbus_tcp.getId().toString());
        evCharger.getExtra().put("idPowerSensor", powerSensor1.getId().toString());
        evCharger.getExtra().put("idController", dynamicExcessController.getId().toString());
        evCharger.getExtra().put("nominalPower", 11);
        evCharger.getExtra().put("isSimulated", false);
        evCharger.getExtra().put("address", "255");

        devices.add(evCharger);

        HardwareComponent electricHeater = new HardwareComponent(
                UUID.randomUUID(),
                "Meine Elektroheizung",
                "Musterfabrik",
                null,
                "POWER_TO_HEAT",
                false,
                null,
                null,
                true
        );
        electricHeater.setExtra(new HashMap<>());


        electricHeater.getExtra().put("idPowerSensor", powerSensor1.getId().toString());
        electricHeater.getExtra().put("nominalPower", 5);
        electricHeater.getExtra().put("isSimulated", false);
        electricHeater.getExtra().put("idController", coverageController.getId().toString());
        electricHeater.getExtra().put("channels", List.of(0));
        electricHeater.getExtra().put("channelConfig", List.of("0", "1"));

        devices.add(electricHeater);

        HardwareComponent lamp = new HardwareComponent(
                UUID.randomUUID(),
                "Meine Lampe",
                "IKEA",
                null,
                "HOUSHOLD_APPLIANCES",
                false,
                null,
                null,
                true
        );
        lamp.setExtra(new HashMap<>());

        lamp.getExtra().put("idRelais", shellyRelais.getId().toString());
        lamp.getExtra().put("idPowerSensor", shellyPowerSensor.getId().toString());
        lamp.getExtra().put("nominalPower", 0.01);
        lamp.getExtra().put("isSimulated", false);
        lamp.getExtra().put("channels", List.of(1));
        lamp.getExtra().put("channelConfig", List.of("0", "1"));
        lamp.getExtra().put("idController", excessController.getId().toString());

        devices.add(lamp);


        Configuration configuration = new Configuration(
                installationName,
                version,
                communicationsList.toArray(new CommunicationChannel[0]),
                sensorList.toArray(new HardwareComponent[0]),
                actuatorList.toArray(new HardwareComponent[0]),
                devices.toArray(new HardwareComponent[0]),
                controllerList.toArray(new Controller[0])
        );

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
}
