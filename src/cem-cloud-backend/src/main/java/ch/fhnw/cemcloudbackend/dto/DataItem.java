package ch.fhnw.cemcloudbackend.dto;

import java.util.UUID;

public class DataItem {
    private String name;
    private Object value;
    private boolean readonly;
    private boolean value_is_reference;
    private UUID id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isValue_is_reference() {
        return value_is_reference;
    }

    public void setValue_is_reference(boolean value_is_reference) {
        this.value_is_reference = value_is_reference;
    }
}
