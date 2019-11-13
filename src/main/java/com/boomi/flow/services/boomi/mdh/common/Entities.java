package com.boomi.flow.services.boomi.mdh.common;

import com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetailsConstants;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityResponse;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntry;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntryConstants;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecord;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Entities {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static MObject createGoldenRecordMObject(String universeId, String id, Multimap<String, Object> entityWrapper, List<GoldenRecord.Link> links) {
        if (entityWrapper == null || entityWrapper.isEmpty()) {
            return null;
        }
        String key = entityWrapper.keys().iterator().next();
        Multimap<String, Object> entity = (Multimap<String, Object>) entityWrapper.get(key).iterator().next();

        List<Property> properties = createPropertiesModel(entity, links);
        properties.add(new Property(GoldenRecordConstants.RECORD_ID_FIELD, id));

        return new MObject(universeId + "-golden-record", id, properties);
    }

    public static MObject createQuarantineMObject(String universeId, QuarantineEntry entry) {
        List<Property> properties = getPropertiesFromEntity(entry.getEntity());

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

    private static List<Property> getPropertiesFromEntity(Map<String, Multimap<String, Object>> entity) {
        if (entity == null || entity.isEmpty()) {
            return new ArrayList<>();
        } else {
            Map.Entry<String, Multimap<String, Object>> entityEntry = entity.entrySet().iterator().next();
            return createPropertiesModel(entityEntry.getValue(), null);
        }
    }

    public static MObject setRandomUniqueIdIfEmpty(MObject object, String idField) {
        if (Strings.isNullOrEmpty(object.getExternalId()) == false) {
            return object;
        }
        // We are requesting an object without id
        String id = UUID.randomUUID().toString();

        // Set the ID property, so it can be referenced in a Flow
        for (Property property : object.getProperties()) {
            if (property.getDeveloperName().equals(idField)) {
                property.setContentValue(id);
            }
        }

        // Set the object's external ID too, which is only used inside Flow itself
        object.setExternalId(id);

        return object;
    }

    public static MObject createMatchMObject(String universeId, Universe universe, MatchEntityResponse.MatchResult result) {
        Multimap<String, Object> entity = ((Multimap) result.getEntity().get(universe.getName()).iterator().next());
        String externalId = entity.get(universe.getIdField())
                .iterator()
                .next()
                .toString();

        Property propertiesMatched = new Property(FuzzyMatchDetailsConstants.MATCH, new ArrayList<>());
        Property propertiesDuplicated = new Property(FuzzyMatchDetailsConstants.DUPLICATE, new ArrayList<>());
        Property propertiesAlreadyLinked = new Property(FuzzyMatchDetailsConstants.ALREADY_LINKED, new ArrayList<>());

        if ("SUCCESS".equals(result.getStatus())) {
            List<MObject> matches = result.getMatch().stream()
                    .map(match -> createMatchesToProperty(universe, match, universe.getIdField(), false))
                    .collect(Collectors.toList());

            propertiesMatched = new Property(FuzzyMatchDetailsConstants.MATCH, matches);

            List<MObject> duplicates = result.getDuplicate().stream()
                    .map(duplicate -> createMatchesToProperty(universe, duplicate, universe.getIdField(), true))
                    .collect(Collectors.toList());

            propertiesDuplicated = new Property(FuzzyMatchDetailsConstants.DUPLICATE, duplicates);

        } else if ("ALREADY_LINKED".equals(result.getStatus())) {
            Multimap<String, Object> entityLinked = result.getEntity();
            propertiesAlreadyLinked = new Property(FuzzyMatchDetailsConstants.ALREADY_LINKED, createAlreadyLinked(universe, entityLinked, universe.getIdField()));
        }

        List<Property> properties =  createPropertiesModel((Multimap<String, Object>) result.getEntity().get(universe.getName()).iterator().next(), null);

        properties.add(new Property(GoldenRecordConstants.SOURCE_ID_FIELD, result.getIdResource()));
        properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, (MObject) null));

        properties.add(propertiesMatched);
        properties.add(propertiesDuplicated);
        properties.add(propertiesAlreadyLinked);

        MObject object = new MObject(universeId + "-match", externalId, properties);
        object.setTypeElementBindingDeveloperName(object.getDeveloperName());

        return object;
    }

    private static MObject createAlreadyLinked(Universe universe, Multimap<String, Object> entity, String idField){
        List<Property> properties = createPropertiesModel((Multimap<String, Object>) entity.get(universe.getName()).iterator().next(), null);

        return new MObject(universe.getId().toString() + "-match", entity.get(idField).toString(),
                properties);
    }

    private static MObject createMatchesToProperty(Universe universe, Multimap<String, Object> entityWrapper, String idField, boolean duplicated){
        Multimap<String, Object> entity = (Multimap<String, Object>)entityWrapper.get(universe.getName()).iterator().next();

        List<Property> properties = createPropertiesModel(entity, null);
        Multimap<String, Object> fuzzyMatchDetails =  (Multimap<String, Object>)entityWrapper.get("fuzzyMatchDetails").iterator().next();

        List<Property> propertiesFuzzy = new ArrayList<>();
        if (fuzzyMatchDetails != null) {
            propertiesFuzzy.add(new Property("Field", fuzzyMatchDetails.get("field").iterator().next()));
            propertiesFuzzy.add(new Property("First", fuzzyMatchDetails.get("first").iterator().next()));
            propertiesFuzzy.add(new Property("Second", fuzzyMatchDetails.get("second").iterator().next()));
            propertiesFuzzy.add(new Property("Method", fuzzyMatchDetails.get("method").iterator().next()));
            propertiesFuzzy.add(new Property("Match Strength", fuzzyMatchDetails.get("matchStrength").iterator().next()));
            propertiesFuzzy.add(new Property("Threshold", fuzzyMatchDetails.get("threshold").iterator().next()));

            MObject fuzzyMatchDetailsObj = new MObject(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, UUID.randomUUID().toString(), propertiesFuzzy);
            properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, fuzzyMatchDetailsObj));
        } else {
            properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, new ArrayList<>()));
        }


        MObject object = new MObject(universe.getId().toString() + "-match", entity.get(idField).toString(), properties);
        object.setTypeElementBindingDeveloperName(object.getDeveloperName());

        return object;
    }

    private static List<Property> createPropertiesModel(Multimap<String, Object> map, List<GoldenRecord.Link> links) {
        ArrayList<Property> properties = new ArrayList<>();

        for (Map.Entry<String, Object> entry:map.entries()) {
            if (entry.getValue() instanceof Multimap) {
                MObject object = new MObject(entry.getKey() + "-child", createPropertiesModel((Multimap<String, Object>) entry.getValue(), null));
                object.setTypeElementBindingDeveloperName(entry.getKey() + "-child");
                object.setExternalId(UUID.randomUUID().toString());
                properties.add(new Property(entry.getKey(), Collections.singletonList(object)));
            } else {
                properties.add(new Property(entry.getKey(), entry.getValue()));
            }
        }

        if (links == null) {
            return  properties;
        }
        // this part is only for Golden Records
        List<MObject> mObjectLinks = links.stream()
                .map(Entities::createMObjectForLink)
                .collect(Collectors.toList());

        properties.add(new Property(GoldenRecordConstants.LINKS_FIELD, mObjectLinks));
        return properties;
    }

    private static MObject createMObjectForLink(GoldenRecord.Link link) {
        List<Property> linkProperties = new ArrayList<>();

        linkProperties.add(new Property("Source", link.getSource()));
        linkProperties.add(new Property("Entity ID", link.getEntityId()));
        linkProperties.add(new Property("Established Date", link.getEstablishedDate().format(formatter)));

        return new MObject(GoldenRecordConstants.LINK, link.getEntityId(), linkProperties);
    }
}
