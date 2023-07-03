package ch.fhnw.cemcloudbackend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(String uploadDirectory, String mqttUsername, String mqttPassword) {
}
