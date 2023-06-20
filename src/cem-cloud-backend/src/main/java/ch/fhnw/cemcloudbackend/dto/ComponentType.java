package ch.fhnw.cemcloudbackend.dto;

import java.util.UUID;

public record ComponentType(UUID id, String name, String family) {
}
