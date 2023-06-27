package ch.fhnw.cemcloudbackend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record CommunicationChannelUpdateRequest(@NotBlank String name, Map<String, Object> parameter) {
}
