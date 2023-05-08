package ch.fhnw.cemcloudbackend.model.set;

public class Set {
    private Sensor[] sensors;

    public Set() {
    }

    public Sensor[] getSensors() {
        return sensors;
    }

    public void setSensors(Sensor[] sensors) {
        this.sensors = sensors;
    }
}
