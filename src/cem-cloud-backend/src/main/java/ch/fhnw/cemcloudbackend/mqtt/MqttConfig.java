package ch.fhnw.cemcloudbackend.mqtt;

public abstract class MqttConfig {

    protected final String broker = "6285dd2901794a3ba0a4a03d1823cf50.s2.eu.hivemq.cloud";
    protected final int qos = 0;
    protected Integer port = 8883; /* Default port */
    protected final String userName = null;
    protected final String password = null;

}