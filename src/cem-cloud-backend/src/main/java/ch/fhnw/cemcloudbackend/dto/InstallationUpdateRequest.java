package ch.fhnw.cemcloudbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record InstallationUpdateRequest(@NotBlank String name, @NotBlank String serialNumber) {
}
