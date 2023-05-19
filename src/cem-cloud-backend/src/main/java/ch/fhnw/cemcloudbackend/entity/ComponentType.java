package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class ComponentType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @ManyToOne
    private ComponentFamily componentFamily;

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

    public ComponentFamily getComponentType() {
        return componentFamily;
    }

    public void setComponentType(ComponentFamily componentFamily) {
        this.componentFamily = componentFamily;
    }
}
