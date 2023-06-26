package ch.fhnw.cemcloudbackend.dto;

public record InstallationPrepareRequest(String serialNumber, String mqttPassword, String backendToken) {
}
