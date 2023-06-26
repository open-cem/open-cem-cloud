package ch.fhnw.cemcloudbackend.dto;

import java.util.UUID;

public record Component(UUID id, String name, String family, UUID channelId, UUID manufacturerId, UUID modelId) {
    public Component(UUID id, String name, String family) {
        this(id, name, family, null, null, null);
    }
}
