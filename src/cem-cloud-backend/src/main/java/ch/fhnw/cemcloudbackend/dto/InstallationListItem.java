package ch.fhnw.cemcloudbackend.dto;

import java.util.UUID;

public record InstallationListItem(UUID id, String name, String serialNumber, String imageUrl) {
}
