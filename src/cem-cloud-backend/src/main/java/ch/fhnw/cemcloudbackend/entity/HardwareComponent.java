package ch.fhnw.cemcloudbackend.entity;

import jakarta.persistence.*;

@Entity
public class HardwareComponent extends Component {
    @OneToOne
    private Model model;
    @OneToOne
    private SmartGridreadyDefinition smartGridreadyDefinition;

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
