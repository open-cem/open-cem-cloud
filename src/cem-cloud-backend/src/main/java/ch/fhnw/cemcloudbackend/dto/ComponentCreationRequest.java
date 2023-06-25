package ch.fhnw.cemcloudbackend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ComponentCreationRequest(@NotNull UUID installationId, @NotNull UUID typeId) {
}
