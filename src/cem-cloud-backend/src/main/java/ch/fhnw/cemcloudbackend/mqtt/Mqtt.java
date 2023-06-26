package ch.fhnw.cemcloudbackend.mqtt;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5RxClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Utf8;

import static java.nio.charset.StandardCharsets.UTF_8;

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

        final Mqtt5RxClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(broker)
                .serverPort(port)
                //.sslWithDefaultConfig()
                .simpleAuth()
                .username(userName)
                .password(password.getBytes(UTF_8))
                .applySimpleAuth()
                .buildRx();

        final Completable disconnect = client.disconnect().doOnComplete(() -> logger.debug("Disconnected successfully"));

        final Flowable<Mqtt5Publish> publish = Flowable.range(0, 10) // 3
                .map(integer -> Mqtt5Publish.builder() // 3.1
                        .topic("installations/123456789") // 3.1
                        .payload(("example #" + integer).getBytes(UTF_8)) // 3.1
                        .qos(MqttQos.AT_LEAST_ONCE) // 3.1
                        .build()) // 3.1
                .doOnComplete(() -> { // 3.2
                    System.out.println("Successfully published!"); // 3.2
                    disconnect.subscribe(); // 3.2
                }).doOnError(throwable -> {
                    System.out.println("Error while publishing!"); // 3.3
                    throwable.printStackTrace(); // 3.3
                });

        client.connect() // 4
                .doOnSuccess(connAck -> {
                    System.out.println("Successfully connected!"); // 4.1
                    client.publish(publish).subscribe(); // 4.2
                }).doOnError(throwable -> { // 4.3
                    System.out.println("Error while connecting!"); // 4.3
                    throwable.printStackTrace(); // 4.3
                }).subscribe(); // 4.4



    }
}
