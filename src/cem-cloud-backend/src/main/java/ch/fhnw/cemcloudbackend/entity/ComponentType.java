package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
public class ComponentType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    private String name;

    private String code;

    @ManyToOne
    private ComponentFamily componentFamily;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
