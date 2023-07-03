package ch.fhnw.cemcloudbackend.model.configuration;

import java.util.Map;
import java.util.UUID;

public class HardwareComponent {
    private UUID id;
    private String name;
    private String manufacturer;
    private String model;
    private String type;
    private Boolean isSmartGridready;
    private UUID smartGridreadyFileId;
    private UUID communicationId;
    private Boolean isLogging;
    private Map<String, Object> extra;

    public HardwareComponent(UUID id, String name, String manufacturer, String model, String type, Boolean isSmartGridready, UUID smartGridreadyFileId, UUID communicationId, Boolean isLogging) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.type = type;
        this.isSmartGridready = isSmartGridready;
        this.smartGridreadyFileId = smartGridreadyFileId;
        this.communicationId = communicationId;
        this.isLogging = isLogging;
    }

    public HardwareComponent(UUID id, String name, String manufacturer, String model, String type, Boolean isSmartGridready, UUID smartGridreadyFileId, UUID communicationId, Boolean isLogging, Map<String, Object> extra) {
        this(id, name, manufacturer, model, type, isSmartGridready, smartGridreadyFileId, communicationId, isLogging);
        this.extra = extra;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsSmartGridready() {
        return isSmartGridready;
    }

    public void setIsSmartGridready(Boolean smartGridreadyCompatible) {
        isSmartGridready = smartGridreadyCompatible;
    }

    public UUID getSmartGridreadyFileId() {
        return smartGridreadyFileId;
    }

    public void setSmartGridreadyFileId(UUID smartGridreadyFileId) {
        this.smartGridreadyFileId = smartGridreadyFileId;
    }

    public UUID getCommunicationId() {
        return communicationId;
    }

    public void setCommunicationId(UUID communicationId) {
        this.communicationId = communicationId;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public Boolean getIsLogging() {
        return isLogging;
    }

    public void setIsLogging(Boolean logging) {
        isLogging = logging;
    }
}
