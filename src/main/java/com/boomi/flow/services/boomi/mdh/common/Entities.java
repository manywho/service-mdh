package com.boomi.flow.services.boomi.mdh.common;

import com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetailsConstants;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityResponse;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntry;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntryConstants;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.google.common.base.Strings;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Entities {

    public static MObject createGoldenRecordMObject(String universeId, String id, Map<String, Map<String, Object>> entity) {
        if (entity == null || entity.isEmpty()) {
            return null;
        }

        var entry = entity.entrySet().iterator().next();
        List<Property> properties = createPropertiesModel(entry.getValue());
        properties.add(new Property(GoldenRecordConstants.RECORD_ID_FIELD, id));

        return new MObject(universeId + "-golden-record", id, properties);
    }

    public static MObject createQuarantineMObject(String universeId, QuarantineEntry entry) {

        if (entry.getEntity() == null || entry.getEntity().isEmpty()) {
            return null;
        }

        var entity = entry.getEntity().entrySet().iterator().next();

        List<Property> properties = createPropertiesModel(entity.getValue());
        properties.add(new Property(QuarantineEntryConstants.CAUSE_FIELD, entry.getCause()));
        properties.add(new Property(QuarantineEntryConstants.CREATED_DATE_FIELD, entry.getCreatedDate()));
        properties.add(new Property(QuarantineEntryConstants.END_DATE_FIELD, entry.getEndDate()));
        properties.add(new Property(QuarantineEntryConstants.REASON_FIELD, entry.getReason()));
        properties.add(new Property(QuarantineEntryConstants.RESOLUTION_FIELD, entry.getResolution()));
        properties.add(new Property(QuarantineEntryConstants.TRANSACTION_ID_FIELD, entry.getTransactionId()));
        properties.add(new Property(QuarantineEntryConstants.SOURCE_ENTITY_ID_FIELD, entry.getSourceEntityId()));
        properties.add(new Property(QuarantineEntryConstants.SOURCE_ID_FIELD, entry.getSourceId()));

        return new MObject(universeId + "-quarantine", entry.getTransactionId(), properties);
    }

    public static MObject setRandomUniqueIdIfEmpty(MObject object, String idField) {
        if (Strings.isNullOrEmpty(object.getExternalId()) == false) {
            return object;
        }
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

    public static MObject createMatchMObject(String universeId, Universe universe, MatchEntityResponse.MatchResult result) {
        var externalId = result.getEntity().get(universe.getName()).get(universe.getIdField()).toString();

        var propertiesMatched = new Property(FuzzyMatchDetailsConstants.MATCH, new ArrayList<>());
        var propertiesDuplicated = new Property(FuzzyMatchDetailsConstants.DUPLICATE, new ArrayList<>());
        var propertiesAlreadyLinked = new Property(FuzzyMatchDetailsConstants.ALREADY_LINKED, new ArrayList<>());

        if ("SUCCESS".equals(result.getStatus())) {
            var matches = result.getMatch().stream()
                    .map(match -> createMatchesToProperty(universe, match, universe.getIdField(), true))
                    .collect(Collectors.toList());

            propertiesMatched = new Property(FuzzyMatchDetailsConstants.MATCH, matches);

            var duplicates = result.getDuplicate().stream()
                    .map(duplicate -> createMatchesToProperty(universe, duplicate, universe.getIdField(), true))
                    .collect(Collectors.toList());

            propertiesDuplicated = new Property(FuzzyMatchDetailsConstants.DUPLICATE, duplicates);

        } else if ("ALREADY_LINKED".equals(result.getStatus())) {
            var entityLinked = result.getEntity().get(universe.getName());
            propertiesAlreadyLinked = new Property(FuzzyMatchDetailsConstants.ALREADY_LINKED, createAlreadyLinked(universe, entityLinked, universe.getIdField()));
        }

        var properties = result.getEntity().get(universe.getName()).entrySet().stream()
                .filter(entry -> entry.getValue() instanceof String)
                .map(entry -> new Property(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        properties.add(new Property(GoldenRecordConstants.SOURCE_ID_FIELD, result.getIdResource()));
        properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, (MObject) null));

        properties.add(propertiesMatched);
        properties.add(propertiesDuplicated);
        properties.add(propertiesAlreadyLinked);

        var object = new MObject(universeId + "-match", externalId, properties);
        object.setTypeElementBindingDeveloperName(object.getDeveloperName());

        return object;
    }

    private static MObject createAlreadyLinked(Universe universe, Map<String, Object> entity, String idField){
        var properties = createPropertiesModel(entity);

        return new MObject(universe.getId().toString() + "-match", entity.get(idField).toString(),
                properties);
    }

    private static MObject createMatchesToProperty(Universe universe, Map<String, Object> matchResults, String idField, boolean addFuzzyMatchDetails){
        var entity = (Map<String, Object>) matchResults.get(universe.getName());
        var properties = createPropertiesModel(entity);

        if (addFuzzyMatchDetails) {
            var result = (Map<String, Object>) matchResults.get("fuzzyMatchDetails");

            var propertiesFuzzy = new ArrayList<Property>();
            if (result != null) {
                propertiesFuzzy.add(new Property("Field", result.get("field")));
                propertiesFuzzy.add(new Property("First", result.get("first")));
                propertiesFuzzy.add(new Property("Second", result.get("second")));
                propertiesFuzzy.add(new Property("Method", result.get("method")));
                propertiesFuzzy.add(new Property("Match Strength", result.get("matchStrength")));
                propertiesFuzzy.add(new Property("Threshold", result.get("threshold")));

                var fuzzyMatchDetails = new MObject(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, UUID.randomUUID().toString(), propertiesFuzzy);
                properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, fuzzyMatchDetails));
            } else {
                properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, new ArrayList<>()));
            }

        }

        var object = new MObject(universe.getId().toString() + "-match", entity.get(idField).toString(), properties);

        object.setTypeElementBindingDeveloperName(object.getDeveloperName());

        return object;
    }

    private static List<Property> createPropertiesModel(Map<String, Object> map) {
        var properties = new ArrayList<Property>();

        for (var entry:map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                MObject object = new MObject(entry.getKey() + "-child", createPropertiesModel((Map<String, Object>) entry.getValue()));
                object.setTypeElementBindingDeveloperName(entry.getKey() + "-child");
                object.setExternalId(UUID.randomUUID().toString());
                properties.add(new Property(entry.getKey(), List.of(object)));
            } else {
                properties.add(new Property(entry.getKey(), entry.getValue()));
            }
        }

        return properties;
    }
}
