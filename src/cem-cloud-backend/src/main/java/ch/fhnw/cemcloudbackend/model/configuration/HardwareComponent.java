package ch.fhnw.cemcloudbackend.model.configuration;

import java.util.Map;
import java.util.UUID;

public class HardwareComponent {
    private UUID id;
    private String name;
    private String manufacturer;
    private String type;
    private String kind;
    private Boolean isSmartGridreadyCompatible;
    private UUID smartGridreadyFileId;
    private UUID communicationId;
    private Map<String, Object> extra;

    public HardwareComponent(UUID id, String name, String manufacturer, String type, String kind, Boolean isSmartGridreadyCompatible, UUID smartGridreadyFileId, UUID communicationId) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.type = type;
        this.kind = kind;
        this.isSmartGridreadyCompatible = isSmartGridreadyCompatible;
        this.smartGridreadyFileId = smartGridreadyFileId;
        this.communicationId = communicationId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Boolean getSmartGridreadyCompatible() {
        return isSmartGridreadyCompatible;
    }

    public void setSmartGridreadyCompatible(Boolean smartGridreadyCompatible) {
        isSmartGridreadyCompatible = smartGridreadyCompatible;
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
}
