package ch.fhnw.cemcloudbackend.model.configuration;

import java.util.Map;
import java.util.UUID;

public class Communication {
    private UUID id;
    private String name;
    private String type;
    private Map<String, Object> extra;

    public Communication(UUID id, String name, String type, Map<String, Object> extra) {
        this.id = id;
        this.name = name;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
