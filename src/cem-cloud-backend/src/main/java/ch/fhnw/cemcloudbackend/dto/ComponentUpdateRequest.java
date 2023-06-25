package ch.fhnw.cemcloudbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record ComponentUpdateRequest(@NotBlank String name) {
}
