package ch.fhnw.cemcloudbackend.dto;

import java.util.UUID;

public record Installation(UUID id, String name, String serialNumber, String imageUrl) {
}
