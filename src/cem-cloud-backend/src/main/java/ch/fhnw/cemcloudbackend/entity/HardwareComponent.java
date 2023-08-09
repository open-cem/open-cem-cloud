package ch.fhnw.cemcloudbackend.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class HardwareComponent extends Component {

    @ManyToOne
    private Manufacturer manufacturer;
    @ManyToOne
    private Model model;
    @OneToOne
    private SmartGridreadyDefinition smartGridreadyDefinition;
    @ManyToOne
    private CommunicationChannel communicationChannel;
    @NotNull
    @ColumnDefault("true")
    boolean isLogging;

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public SmartGridreadyDefinition getSmartGridreadyDefinition() {
        return smartGridreadyDefinition;
    }

    public void setSmartGridreadyDefinition(SmartGridreadyDefinition smartGridreadyDefinition) {
        this.smartGridreadyDefinition = smartGridreadyDefinition;
    }

    public CommunicationChannel getCommunicationChannel() {
        return communicationChannel;
    }

    public void setCommunicationChannel(CommunicationChannel communicationChannel) {
        this.communicationChannel = communicationChannel;
    }

    public boolean getLogging() {
        return isLogging;
    }

    public void setLogging(boolean isLogging) {
        this.isLogging = isLogging;
    }
}
