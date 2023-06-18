package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ComponentParameterMeta extends ParameterMeta {
    @ManyToOne
    private ComponentFamily referenceComponentFamily;
    @ManyToOne
    private ComponentType ComponentType;

    public ComponentFamily getReferenceComponentFamily() {
        return referenceComponentFamily;
    }

    public void setReferenceComponentFamily(ComponentFamily referenceType) {
        this.referenceComponentFamily = referenceType;
    }

    public ComponentType getComponentType() {
        return ComponentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.ComponentType = componentType;
    }
}
