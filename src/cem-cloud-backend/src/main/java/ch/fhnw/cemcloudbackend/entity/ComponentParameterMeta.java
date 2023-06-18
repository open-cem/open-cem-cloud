package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ComponentParameterMeta extends ParameterMeta {
    @ManyToOne
    private ComponentFamily referenceComponentFamily;
    @ManyToOne
    private ComponentType componentType;

    public ComponentFamily getReferenceComponentFamily() {
        return referenceComponentFamily;
    }

    public void setReferenceComponentFamily(ComponentFamily referenceType) {
        this.referenceComponentFamily = referenceType;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }
}
