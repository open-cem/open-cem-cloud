package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
public class MqttAccessControl {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @NotEmpty
    private String topic;

    @ManyToOne
    private InstallationCredentials installationCredential;

    @Basic
    private int accessLevel;

    @Transient
    private AccessLevel accessLevelEnum;

    @PostLoad
    void fillTransient() {
        if (accessLevel > 0) {
            this.accessLevelEnum = AccessLevel.fromValue(accessLevel);
        }
    }

    @PrePersist
    void fillPersistent() {
        if (accessLevelEnum != null) {
            this.accessLevel = accessLevelEnum.getValue();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public InstallationCredentials getInstallationCredential() {
        return installationCredential;
    }

    public void setInstallationCredential(InstallationCredentials installationCredential) {
        this.installationCredential = installationCredential;
    }

    public AccessLevel getAccessLevel() {
        return accessLevelEnum;
    }

    public void setAccessLevel(AccessLevel accessLevelEnum) {
        this.accessLevelEnum = accessLevelEnum;
    }

    public enum AccessLevel {
        READ(1),
        WRITE(2),
        SUBSCRIBE(4);

        private final int value;

        AccessLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static AccessLevel fromValue(int value) {
            for (AccessLevel accessLevel : AccessLevel.values()) {
                if (accessLevel.value == value) {
                    return accessLevel;
                }
            }
            throw new IllegalArgumentException("Invalid access level value: " + value);
        }
    }
}
