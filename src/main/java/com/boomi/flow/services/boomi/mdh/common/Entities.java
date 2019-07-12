package com.boomi.flow.services.boomi.mdh.common;

import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Entities {
    public static MObject createGoldenRecordMObject(String universeId, String id, Map<String,  Map<String, Object>> entity) {
        if (entity == null || entity.isEmpty()) {
            return null;
        }

        var entry = entity.entrySet().iterator().next();
        List<Property> properties = createPropertiesForGoldenRecord(id, entry.getValue());

        return new MObject(universeId + " golden-record", id, properties);
    }

    private static List<Property> createPropertiesForGoldenRecord( String id, Map<String, Object> map) {
        return map.entrySet().stream()
                .map(field -> {
                    if (field.getValue() instanceof Map == false) {

                        return new Property(field.getKey(), field.getValue());
                    } else {
                        MObject object = new MObject(field.getKey(), id, createPropertiesFromMap(id, (Map<String, Object>) field.getValue()));

                        return new Property(field.getKey(), object);
                    }
                }).collect(Collectors.toList());
    }

    private static List<Property> createPropertiesFromMap( String id, Map<String, Object> map) {
        return map.entrySet().stream()
                .map(field -> {
                    if (field.getValue() instanceof Map == false) {

                        return new Property(field.getKey(), field.getValue());
                    } else {
                        MObject object = new MObject(field.getKey(), id, createPropertiesFromMap(id, (Map<String, Object>) field.getValue()));

                        return new Property(field.getKey(), object);
                    }
                }).collect(Collectors.toList());
    }

    public static MObject createEntityMObject(String id, String type, Map<String,  Map<String, Object>> entity) {
        if (entity == null || entity.isEmpty()) {
            return null;
        }

        var entry = entity.entrySet().iterator().next();

        var properties = createPropertiesFromMap(id, entry.getValue());

        return new MObject(String.format("%s %s", entry.getKey(), type), id, properties);
    }
}
