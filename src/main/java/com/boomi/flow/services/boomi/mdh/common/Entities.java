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
        List<Property> properties = createPropertiesModel(id, entry.getValue());

        return new MObject(universeId + " golden-record", id, properties);
    }

    public static MObject createQuarantineMObject(String universeId, String id, Map<String,  Map<String, Object>> entity) {
        if (entity == null || entity.isEmpty()) {
            return null;
        }

        var entry = entity.entrySet().iterator().next();
        List<Property> properties = createPropertiesModel(id, entry.getValue());

        return new MObject(universeId + " quarantine", id, properties);
    }

    private static List<Property> createPropertiesModel(String id, Map<String, Object> map) {
        return map.entrySet().stream()
                // Todo: we ignored child objects until get support in engine for child object bindings
                .filter(field -> field.getValue() instanceof Map == false)
                .map(field -> {
                    if (field.getValue() instanceof Map == false) {

                        return new Property(field.getKey(), field.getValue());
                    } else {
                        MObject object = new MObject(field.getKey(), id, createPropertyType(id, (Map<String, Object>) field.getValue()));

                        return new Property(field.getKey(), object);
                    }
                }).collect(Collectors.toList());
    }

    private static List<Property> createPropertyType(String id, Map<String, Object> map) {
        return map.entrySet().stream()
                .map(field -> {
                    if (field.getValue() instanceof Map == false) {

                        return new Property(field.getKey(), field.getValue());
                    } else {

                        MObject object = new MObject(field.getKey(), id, createPropertyType(id, (Map<String, Object>) field.getValue()));

                        return new Property(field.getKey(), object);
                    }
                }).collect(Collectors.toList());
    }

}
