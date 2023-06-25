package ch.fhnw.cemcloudbackend.entity;

import ch.fhnw.cemcloudbackend.entity.converter.ParameterConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
public class CommunicationChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @NotEmpty
    private String name;

    @ManyToOne
    private Installation installation;

    @OneToOne
    private CommunicationChannelType typ;

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

    public Installation getInstallation() {
        return installation;
    }

    public void setInstallation(Installation installation) {
        this.installation = installation;
    }

    public CommunicationChannelType getTyp() {
        return typ;
    }

    public void setTyp(CommunicationChannelType typ) {
        this.typ = typ;
    }

    public Map<String, Object> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, Object> parameter) {
        this.parameter = parameter;
    }
}
