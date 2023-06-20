package ch.fhnw.cemcloudbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record InstallationCreateRequest(@NotBlank String name, @NotBlank String serialNumber) {
}
