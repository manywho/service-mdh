package com.boomi.flow.services.boomi.mdh.common;

import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntry;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntryConstants;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Entities {

    public static MObject createGoldenRecordMObject(String universeId, String id, Map<String,  Map<String, Object>> entity) {
        if (entity == null || entity.isEmpty()) {
            return null;
        }

        var entry = entity.entrySet().iterator().next();
        List<Property> properties = createPropertiesModel(id, entry.getValue());

        return new MObject(universeId + "-golden-record", id, properties);
    }

    public static MObject createQuarantineMObject(String universeId, QuarantineEntry entry) {

        if (entry.getEntity() == null || entry.getEntity().isEmpty()) {
            return null;
        }

        var entity = entry.getEntity().entrySet().iterator().next();

        List<Property> properties = createPropertiesModel(entry.getSourceEntityId(), entity.getValue());
        properties.add(new Property(QuarantineEntryConstants.CAUSE_FIELD, entry.getCause()));
        properties.add(new Property(QuarantineEntryConstants.CREATED_DATE_FIELD, entry.getCreatedDate()));
        properties.add(new Property(QuarantineEntryConstants.END_DATE_FIELD, entry.getEndDate()));
        properties.add(new Property(QuarantineEntryConstants.REASON_FIELD, entry.getReason()));
        properties.add(new Property(QuarantineEntryConstants.RESOLUTION_FIELD, entry.getResolution()));
        properties.add(new Property(QuarantineEntryConstants.TRANSACTION_ID_FIELD, entry.getTransactionId()));
        properties.add(new Property(QuarantineEntryConstants.SOURCE_ENTITY_ID_FIELD, entry.getSourceEntityId()));

        return new MObject(universeId + "-quarantine", entry.getTransactionId(), properties);
    }

    public static MObject addRandomUniqueId(MObject object, String idField) {
        // We are requesting an object without id
        var id = UUID.randomUUID().toString();

        // Set the ID property, so it can be referenced in a Flow
        for (var property : object.getProperties()) {
            if (property.getDeveloperName().equals(idField)) {
                property.setContentValue(id);
            }
        }

        // Set the object's external ID too, which is only used inside Flow itself
        object.setExternalId(id);

        return object;
    }

    private static List<Property> createPropertiesModel(String id, Map<String, Object> map) {
        return map.entrySet().stream()
                // Todo: we ignored child objects until get support in engine for child object bindings
                .filter(field -> field.getValue() instanceof Map == false)
                .map(field -> new Property(field.getKey(), field.getValue())).collect(Collectors.toList());
    }
}
