package ch.fhnw.cemcloudbackend.mqtt;

public abstract class MqttConfig {

    protected final String broker = "localhost";
    protected final int qos = 0;
    protected Integer port = 1883;
    protected final String userName = "cem-cloud-backend";
    protected final String password = "testing123";

}