package com.boomi.flow.services.boomi.mdh.match;

import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import java.util.*;
import java.util.stream.Collectors;

public class MatchResponseMapper {

    public static MObject createMobjectFromResult(String universeId, Universe universe, MatchEntityResponse.MatchResult result) {
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
            propertiesAlreadyLinked = new Property(FuzzyMatchDetailsConstants.ALREADY_LINKED,
                    Arrays.asList(createAlreadyLinked(universe, entityLinked, universe.getIdField())));
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
        List<Property> properties = entity.entrySet().stream()
                .filter(element -> element.getValue() instanceof Map == false)
                .map(element -> new Property(element.getKey(), element.getValue()))
                .collect(Collectors.toList());

        return new MObject(universe.getId().toString() + "-match", entity.get(idField).toString(),
                properties);
    }

    private static MObject createMatchesToProperty(Universe universe, Map<String, Object> matchResults, String idField, boolean addFuzzyMatchDetails){
        var entity = (Map<String, Object>) matchResults.get(universe.getName());

        var properties = entity.entrySet().stream()
                .filter(element-> element.getValue() instanceof Map == false)
                .map(element -> new Property(element.getKey(), element.getValue()))
                .collect(Collectors.toList());

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
            }

            var fuzzyMatchDetails = new MObject(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, UUID.randomUUID().toString(), propertiesFuzzy);
            properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, fuzzyMatchDetails));

        }

        var object = new MObject(universe.getId().toString() + "-match", entity.get(idField).toString(), properties);

        object.setTypeElementBindingDeveloperName(object.getDeveloperName());

        return object;
    }
}
