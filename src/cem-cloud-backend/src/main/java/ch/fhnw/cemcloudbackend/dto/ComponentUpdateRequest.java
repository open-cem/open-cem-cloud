package ch.fhnw.cemcloudbackend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ComponentUpdateRequest(@NotBlank String name, UUID manufacturerId, UUID modelId) {
}
