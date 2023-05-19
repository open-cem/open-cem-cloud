package ch.fhnw.cemcloudbackend.entity;

import ch.fhnw.cemcloudbackend.entity.converter.ParameterConverter;
import jakarta.persistence.*;

import java.util.Map;
import java.util.UUID;

@Entity
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @ManyToOne
    private ComponentType type;
    @Convert(converter = ParameterConverter.class)
    private Map<String, Object> parameter;

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

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    public Map<String, Object> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, Object> parameter) {
        this.parameter = parameter;
    }
}
