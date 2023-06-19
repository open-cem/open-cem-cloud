package ch.fhnw.cemcloudbackend.dto;

import java.util.UUID;

public record ComponentListItem(UUID id, String name, String family) {
}
