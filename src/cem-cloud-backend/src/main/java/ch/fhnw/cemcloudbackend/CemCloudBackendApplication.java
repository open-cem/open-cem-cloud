package ch.fhnw.cemcloudbackend;

import ch.fhnw.cemcloudbackend.configuration.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
@ImportResource("classpath:applicationContext.xml")
public class CemCloudBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CemCloudBackendApplication.class, args);
    }

}
