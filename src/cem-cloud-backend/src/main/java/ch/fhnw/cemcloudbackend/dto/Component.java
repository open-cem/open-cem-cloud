package ch.fhnw.cemcloudbackend.dto;

import java.util.Map;
import java.util.UUID;

public record Component(UUID id, String name, String family, UUID channelId, UUID manufacturerId, UUID modelId, Map<String, Object> parameter) {
    public Component(UUID id, String name, String family, Map<String, Object> parameter) {
        this(id, name, family, null, null, null, parameter);
    }
}
