package ch.fhnw.cemcloudbackend.dto;

import java.util.UUID;

public record ParameterMeta(String name, String label, String type, String listTyp, UUID referenceFamilyId) {
}
