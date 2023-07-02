package ch.fhnw.cemcloudbackend.mqtt;

import ch.fhnw.cemcloudbackend.configuration.ApplicationProperties;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.codec.Utf8;

public class Mqtt extends MqttConfig{

    private final Logger logger = LoggerFactory.getLogger(Mqtt.class);

    public Mqtt(ApplicationProperties applicationProperties) {
        this.userName = applicationProperties.mqttUsername();
        this.password = applicationProperties.mqttPassword();
    }

    public void sendMessage(String topic, String content) {

        if (topic == null) {
            throw new IllegalArgumentException("topic can not be null");
        }

        if (content == null) {
            throw new IllegalArgumentException("content can not be null");
        }

        if (userName == null) {
            throw new IllegalArgumentException("userName can not be null");
        }

        if (password == null) {
            throw new IllegalArgumentException("password can not be null");
        }

        final Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(broker)
                .serverPort(port)
                .buildBlocking();

        // connect to MQTT broker with TLS and username/pw
        client.connectWith()
                .simpleAuth()
                .username(userName)
                .password(Utf8.encode(password))
                .applySimpleAuth()
                .send();
        logger.debug("Connected successfully");

        // publish a message to the topic
        client.publishWith()
                .topic(topic)
                .payload(Utf8.encode(content))
                .send();

        client.disconnect();



    }
}