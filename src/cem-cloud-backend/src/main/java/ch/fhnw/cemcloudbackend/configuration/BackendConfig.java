package ch.fhnw.cemcloudbackend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.fhnw.cemcloudbackend.mqtt.Mqtt;
import ch.fhnw.cemcloudbackend.repository.InstallationImageRepository;
import ch.fhnw.cemcloudbackend.repository.SmartGridReadyXmlRepository;
import ch.fhnw.cemcloudbackend.repository.WizardSetRepository;

@Configuration
public class BackendConfig {

    @Bean
    public Mqtt mqtt(ApplicationProperties applicationProperties) {
        return new Mqtt(applicationProperties);
    }
}
