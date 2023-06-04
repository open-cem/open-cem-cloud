package ch.fhnw.cemcloudbackend.entity.converter;

import jakarta.persistence.AttributeConverter;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class ParameterConverter implements AttributeConverter<Map<String, Object>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }

        return new Yaml().dumpAsMap(attribute);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        return new Yaml().load(dbData);
    }
}
