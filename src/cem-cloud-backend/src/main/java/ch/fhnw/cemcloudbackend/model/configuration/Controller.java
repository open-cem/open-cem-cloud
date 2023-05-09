package ch.fhnw.cemcloudbackend.model.configuration;

import java.util.Map;
import java.util.UUID;

public class Controller {
    private UUID id;
    private String name;
    private String kind;
    private Map<String, Object> extra;

    public Controller(UUID id, String name, String kind, Map<String, Object> extra) {
        this.id = id;
        this.name = name;
        this.kind = kind;
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
