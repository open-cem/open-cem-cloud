package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.*;

@Entity
public class HardwareComponent extends Component {

    @ManyToOne
    private Manufacturer manufacturer;
    @ManyToOne
    private Model model;
    @OneToOne
    private SmartGridreadyDefinition smartGridreadyDefinition;

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
}
