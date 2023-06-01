package ch.fhnw.cemcloudbackend.mqtt;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Utf8;

public class Mqtt extends MqttConfig{

    private Logger logger = LoggerFactory.getLogger(Mqtt.class);
    public Mqtt(){}

    public void sendMessage(String topic, String content) {

        if (userName == null) {
            logger.error("No username provided");
            return;
        }
        if (password == null) {
            logger.error("No password provided");
            return;
        }

        final Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(broker)
                .serverPort(port)
                //.sslWithDefaultConfig()
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
