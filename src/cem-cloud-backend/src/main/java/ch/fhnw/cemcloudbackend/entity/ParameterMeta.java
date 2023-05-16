package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class ParameterMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String label;
    @Enumerated(EnumType.STRING)
    private ParameterType type;
    @Enumerated(EnumType.STRING)
    private ParameterType listType;
    @OneToOne
    private HardwareComponentType referenceType;
    @OneToOne
    private ComponentType componentType;

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    public ParameterType getListType() {
        return listType;
    }

    public void setListType(ParameterType listType) {
        this.listType = listType;
    }

    public HardwareComponentType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(HardwareComponentType referenceType) {
        this.referenceType = referenceType;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public enum ParameterType {
        TEXT("TEXT"),
        NUMBER("NUMBER"),
        BOOL("BOOL"),
        REFERENCE("REFERENCE"),
        LIST("LIST");

        private final String name;

        ParameterType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
