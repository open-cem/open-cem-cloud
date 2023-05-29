package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.entity.*;
import ch.fhnw.cemcloudbackend.mqtt.Mqtt;
import ch.fhnw.cemcloudbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class HelloController {

    @Autowired
    private ManufacturerRepository manufacturers;
    @Autowired
    private ModelRepository models;
    @Autowired
    private ComponentTypeRepository componentTypes;
    @Autowired
    private ComponentSubTypeRepository componentSubTypes;
    @Autowired
    private HardwareComponentRepository components;
    @Autowired
    private ParameterMetaRepository parameterMetas;

    @GetMapping("/test")
    public ResponseEntity<Iterable<HardwareComponent>> test(JwtAuthenticationToken auth) {

        UUID cID = UUID.fromString("2303aa82-fd14-411b-8166-ebcec59f6e15");
        Optional<ComponentFamily> optionalComponentType = componentTypes.findById(cID);
        ComponentFamily componentFamily;
        if (optionalComponentType.isEmpty()) {
            componentFamily = new ComponentFamily();
            componentFamily.setName("Device");
            componentFamily = componentTypes.save(componentFamily);
        } else {
            componentFamily = optionalComponentType.get();
        }

        UUID sID = UUID.fromString("e52e4269-95fd-469b-a1a9-6599bda30d4e");
        Optional<ComponentType> oType = componentSubTypes.findById(sID);
        ComponentType type;
        if (oType.isEmpty()) {
            type = new ComponentType();
            type.setId(sID);
            type.setComponentType(componentFamily);
            type.setName("PV_PLANT");
            type = componentSubTypes.save(type);
        } else {
            type = oType.get();
        }

        UUID manufacturerId = UUID.fromString("c0c653c4-92b6-4f42-a623-8f94fac04480");
        Optional<Manufacturer> optionalManufacturer = manufacturers.findById(manufacturerId);
        Manufacturer manufacturer;
        if (optionalManufacturer.isEmpty()) {
            manufacturer = new Manufacturer();
            manufacturer.setId(manufacturerId);
            manufacturer.setName("Swisssolar");
            manufacturer = manufacturers.save(manufacturer);
        } else {
            manufacturer = optionalManufacturer.get();
        }

        UUID modelId = UUID.fromString("340bbf33-a998-41c2-b9a0-08bf3c9045c9");
        Optional<Model> optionalModel = models.findById(modelId);
        Model model;
        if (optionalModel.isEmpty()) {
            model = new Model();
            model.setId(modelId);
            model.setManufacturer(manufacturer);
            model.setName("Panel 2100");
            model = models.save(model);
        } else {
            model = optionalModel.get();
        }

        UUID deviceId = UUID.fromString("444e5150-a79e-4e8e-83e9-19ff5197ef1a");
        Optional<HardwareComponent> optionalDevice = components.findById(deviceId);
        HardwareComponent device;
        if (optionalDevice.isEmpty()) {
            device = new HardwareComponent();
            device.setId(deviceId);
            device.setName("PV-Anlage West");
            device.setModel(model);
            device.setType(type);
            device.setSmartGridready(false);
            Map<String, Object> parameter = new HashMap<>();
            parameter.put("idPowerSensor", "b4791473-37eb-4bea-8255-4a29701245cc");
            parameter.put("isSimulated", false);
            parameter.put("maxPower", 5);
            device.setParameter(parameter);
            device = components.save(device);
        } else {
            device = optionalDevice.get();
        }

        UUID pid = UUID.fromString("8d41e085-bf6a-416b-99da-d0ba2d74632f");
        Optional<ParameterMeta> oParam = parameterMetas.findById(pid);
        ParameterMeta meta;
        if (oParam.isEmpty()) {
            meta = new ParameterMeta();
            meta.setName("idPowerSensor");
            meta.setComponentSubType(type);
            meta.setType(ParameterMeta.ParameterType.REFERENCE);
            meta.setReferenceType(componentFamily);
            meta.setLabel("Power Sensor");
            meta = parameterMetas.save(meta);
        } else {
            meta = oParam.get();
        }

        Iterable<HardwareComponent> results = components.findAllByTypeComponentFamilyName("Device");

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @PostMapping("/sendEvent")
    public ResponseEntity<String> sendEvent(@RequestParam String message) {
        System.out.println(message);

        Mqtt client = new Mqtt();
        try {

            client.sendMessage("installations/123456789", message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
