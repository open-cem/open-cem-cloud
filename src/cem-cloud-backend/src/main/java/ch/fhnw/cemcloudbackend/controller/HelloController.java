package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.entity.*;
import ch.fhnw.cemcloudbackend.model.set.Set;
import ch.fhnw.cemcloudbackend.mqtt.Mqtt;
import ch.fhnw.cemcloudbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.util.*;

@RestController
public class HelloController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SensorRepository sensorRepository;


    @Autowired
    private ManufacturerRepository manufacturers;
    @Autowired
    private ModelRepository models;
    @Autowired
    private DeviceTypeRepository deviceTypes;
    @Autowired
    private DeviceRepository devices;
    @Autowired
    private ParameterMetaRepository parameterMetas;
    @Autowired
    private HardwareComponentTypeRepository hardwareComponentTypes;

    @GetMapping("/test")
    public ResponseEntity<Device> test(JwtAuthenticationToken auth) {

        UUID typeId = UUID.fromString("e52e4269-95fd-469b-a1a9-6599bda30d4e");
        Optional<DeviceType> oType = deviceTypes.findById(typeId);
        DeviceType type;
        if (oType.isEmpty()) {
            type = new DeviceType();
            type.setId(typeId);
            type.setName("PV_PLANT");
            type = deviceTypes.save(type);
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
        Optional<Device> optionalDevice = devices.findById(deviceId);
        Device device;
        if (optionalDevice.isEmpty()) {
            device = new Device();
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
            device = devices.save(device);
        } else {
            device = optionalDevice.get();
        }

        Optional<HardwareComponentType> oHWType = hardwareComponentTypes.findById("DEVICE");
        HardwareComponentType hwType;
        if (oHWType.isEmpty()) {
            hwType = new HardwareComponentType();
            hwType.setName("DEVICE");
            hwType = hardwareComponentTypes.save(hwType);
        } else {
            hwType = oHWType.get();
        }

        UUID pid = UUID.fromString("8d41e085-bf6a-416b-99da-d0ba2d74632f");
        Optional<ParameterMeta> oParam = parameterMetas.findById(pid);
        ParameterMeta meta;
        if (oParam.isEmpty()) {
            meta = new ParameterMeta();
            meta.setName("idPowerSensor");
            meta.setComponentType(type);
            meta.setType(ParameterMeta.ParameterType.REFERENCE);
            meta.setReferenceType(hwType);
            meta.setLabel("Power Sensor");
            meta = parameterMetas.save(meta);
        } else {
            meta = oParam.get();
        }


        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @GetMapping("/hello")
    public User getHello(JwtAuthenticationToken auth) {
        Optional<User> user = userRepository.findById(UUID.fromString(auth.getName()));
        User u;
        if (user.isEmpty()) {
            u = new User();
            u.setId(UUID.fromString(auth.getName()));
            u.setName(auth.getTokenAttributes().get("name").toString());
            userRepository.save(u);
        } else {
            u = user.get();
        }

        return u;
    }

    @PostMapping("/upload")
    public ResponseEntity<Set> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".yml")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Yaml yaml = new Yaml(new Constructor(Set.class));
        Set set = yaml.load(file.getInputStream());

        Sensor sensor = new Sensor();
        sensor.setId(UUID.randomUUID());
        sensor.setName(set.getSensors()[0].getName());
        //sensor.setManufacturer(set.getSensors()[0].getManufacturer());
        //sensor.setModel(set.getSensors()[0].getModel());
        //List<Parameter> parameters = new ArrayList<>();
        //Parameter parameter = new Parameter();
        //parameter.setId(UUID.randomUUID());
        //parameter.setKey("max_power");
        //parameter.setValue("10");
        int i = 10;
        //parameters.add(parameter);
        //sensor.setParameters(parameters);

        sensorRepository.save(sensor);

        return new ResponseEntity<>(set, HttpStatus.OK);
    }

    @PostMapping("/sendEvent")
    public ResponseEntity<String> sendEvent(@RequestParam String message) {
        System.out.println(message);

        Mqtt client = new Mqtt();
        try {

            client.sendMessage("installation/123456789", message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
