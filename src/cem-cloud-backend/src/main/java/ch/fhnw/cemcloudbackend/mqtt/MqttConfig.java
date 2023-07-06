package ch.fhnw.cemcloudbackend.mqtt;

public abstract class MqttConfig {

    protected final String host;
    protected static final int QOS = 0;
    protected static final int PORT = 1883;
    protected final String userName;
    protected final String password;

    public MqttConfig(String host, String userName, String password) {
        this.host = host;
        this.userName = userName;
        this.password = password;
    }
}