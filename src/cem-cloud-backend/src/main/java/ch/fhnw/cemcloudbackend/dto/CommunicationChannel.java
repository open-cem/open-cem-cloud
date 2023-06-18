package ch.fhnw.cemcloudbackend.dto;

import java.util.Map;
import java.util.UUID;

public record CommunicationChannel(UUID id, String name, Map<String, Object> parameter) {
}
