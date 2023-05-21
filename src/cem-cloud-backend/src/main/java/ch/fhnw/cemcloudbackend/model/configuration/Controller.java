package ch.fhnw.cemcloudbackend.model.configuration;

import java.util.Map;
import java.util.UUID;

public class Controller {
    private UUID id;
    private String type;
    private Map<String, Object> extra;

    public Controller(UUID id, String type, Map<String, Object> extra) {
        this.id = id;
        this.type = type;
        this.extra = extra;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
