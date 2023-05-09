package ch.fhnw.cemcloudbackend.model.configuration;

import java.util.Date;

public class Configuration {
    private String installationName;
    private Date creationTimestamp;
    private int version;

    private Communication[] communication;

    private HardwareComponent[] sensors;
    private HardwareComponent[] actuators;
    private HardwareComponent[] devices;

    private Controller[] controllers;


    public Configuration(String installationName,
                         int version,
                         Communication[] communication,
                         HardwareComponent[] sensors,
                         HardwareComponent[] actuators,
                         HardwareComponent[] devices,
                         Controller[] controllers) {
        this.installationName = installationName;
        this.creationTimestamp = new Date();
        this.version = version;
        this.communication = communication;
        this.sensors = sensors;
        this.actuators = actuators;
        this.devices = devices;
        this.controllers = controllers;
    }

    public String getInstallationName() {
        return installationName;
    }

    public void setInstallationName(String installationName) {
        this.installationName = installationName;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public HardwareComponent[] getSensors() {
        return sensors;
    }

    public void setSensors(HardwareComponent[] sensors) {
        this.sensors = sensors;
    }

    public HardwareComponent[] getActuators() {
        return actuators;
    }

    public void setActuators(HardwareComponent[] actuators) {
        this.actuators = actuators;
    }

    public HardwareComponent[] getDevices() {
        return devices;
    }

    public void setDevices(HardwareComponent[] devices) {
        this.devices = devices;
    }

    public Communication[] getCommunication() {
        return communication;
    }

    public void setCommunication(Communication[] communication) {
        this.communication = communication;
    }

    public Controller[] getControllers() {
        return controllers;
    }

    public void setControllers(Controller[] controllers) {
        this.controllers = controllers;
    }
}
