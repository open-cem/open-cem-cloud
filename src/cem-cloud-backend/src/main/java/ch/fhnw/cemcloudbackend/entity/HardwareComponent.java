package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.*;

@Entity
public abstract class HardwareComponent extends Component {
    @OneToOne
    private Model model;
    private boolean isSmartGridready;
    @OneToOne
    private SmartGridreadyDefinition smartGridreadyDefinition;
    @OneToOne
    private HardwareComponentType type;

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public boolean isSmartGridready() {
        return isSmartGridready;
    }

    public void setSmartGridready(boolean smartGridready) {
        isSmartGridready = smartGridready;
    }

    public SmartGridreadyDefinition getSmartGridreadyDefinition() {
        return smartGridreadyDefinition;
    }

    public void setSmartGridreadyDefinition(SmartGridreadyDefinition smartGridreadyDefinition) {
        this.smartGridreadyDefinition = smartGridreadyDefinition;
    }
}
