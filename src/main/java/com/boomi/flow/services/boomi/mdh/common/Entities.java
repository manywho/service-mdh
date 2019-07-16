package com.boomi.flow.services.boomi.mdh.common;

import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntry;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntryConstants;
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

    public static MObject createQuarantineMObject(String universeId, QuarantineEntry entry, Map<String,  Map<String, Object>> entity) {
        if (entity == null || entity.isEmpty()) {
            return null;
        }

        var entry1 = entity.entrySet().iterator().next();

        List<Property> properties = createPropertiesModel(entry.getSourceEntityId(), entry1.getValue());
        properties.add(new Property(QuarantineEntryConstants.CAUSE_FIELD, entry.getCause()));
        properties.add(new Property(QuarantineEntryConstants.CREATED_DATE_FIELD, entry.getCreatedDate()));
        properties.add(new Property(QuarantineEntryConstants.END_DATE_FIELD, entry.getEndDate()));
        properties.add(new Property(QuarantineEntryConstants.REASON_FIELD, entry.getReason()));
        properties.add(new Property(QuarantineEntryConstants.RESOLUTION_FIELD, entry.getResolution()));
        properties.add(new Property(QuarantineEntryConstants.TRANSACTION_ID_FIELD, entry.getTransactionId()));
        properties.add(new Property(QuarantineEntryConstants.SOURCE_ENTITY_ID_FIELD, entry.getSourceEntityId()));

        return new MObject(universeId + " quarantine", entry.getTransactionId(), properties);
    }

    private static List<Property> createPropertiesModel(String id, Map<String, Object> map) {
        return map.entrySet().stream()
                // Todo: we ignored child objects until get support in engine for child object bindings
                .filter(field -> field.getValue() instanceof Map == false)
                .map(field -> {
                    if (field.getValue() instanceof Map == false) {

                        return new Property(field.getKey(), field.getValue());
                    } else {
                        MObject object = new MObject(field.getKey(), id, createPropertiesModel(id, (Map<String, Object>) field.getValue()));

                        return new Property(field.getKey(), object);
                    }
                }).collect(Collectors.toList());
    }
}
