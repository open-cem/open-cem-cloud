package ch.fhnw.cemcloudbackend.dto;

public record InstallationCredential(String serialNumber, String mqttPassword, String backendToken) {
}
