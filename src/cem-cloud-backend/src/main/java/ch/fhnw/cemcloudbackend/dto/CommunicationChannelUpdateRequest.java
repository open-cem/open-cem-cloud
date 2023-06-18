package ch.fhnw.cemcloudbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record CommunicationChannelUpdateRequest(@NotBlank String name) {
}
