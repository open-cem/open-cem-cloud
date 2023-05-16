package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.*;

@Entity
public class HardwareComponentType {
    @Id
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
