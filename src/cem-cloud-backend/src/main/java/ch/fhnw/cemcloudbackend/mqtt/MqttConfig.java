package ch.fhnw.cemcloudbackend.mqtt;

public abstract class MqttConfig {

    protected final static String HOST = "localhost";
    protected static final int QOS = 0;
    protected static final int PORT = 1883;
    protected final String userName;
    protected final String password;

    public MqttConfig(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}