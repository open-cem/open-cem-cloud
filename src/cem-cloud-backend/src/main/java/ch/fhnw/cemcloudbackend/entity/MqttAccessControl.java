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

    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;

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
    }
}
