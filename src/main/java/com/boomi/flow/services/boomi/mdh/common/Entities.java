package com.boomi.flow.services.boomi.mdh.common;

import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Entities {
    public static MObject createEntityMObject(String id, String type, Map<String, Map<String, Object>> entity) {
        if (entity == null || entity.isEmpty()) {
            return null;
        }

        var entry = entity.entrySet().iterator().next();

        var properties = createPropertiesFromMap(entry.getValue());

        return new MObject(String.format("%s %s", entry.getKey(), type), id, properties);
    }

    private static List<Property> createPropertiesFromMap(Map<String, Object> map) {
        // We don't really support any nested objects or lists yet, so we filter them out and map to a type property
        return map.entrySet().stream()
                .filter(field -> (field.getValue() instanceof Map) == false)
                .map(field -> new Property(field.getKey(), field.getValue()))
                .collect(Collectors.toList());
    }
}
