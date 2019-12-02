package com.boomi.flow.services.boomi.mdh.common;

import com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetailsConstants;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityResponse;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntry;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntryConstants;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecord;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import java.util.*;
import java.util.stream.Collectors;

public class Entities {

    public static MObject createGoldenRecordMObject(String universeId, String id, MObject mObject, List<GoldenRecord.Link> links) {
        if (mObject == null) {
            return null;
        }

        List<Property> properties = mObject.getProperties();

        // this part is only for Golden Records
        List<MObject> mObjectLinks = links.stream()
                .map(Entities::createMObjectForLink)
                .collect(Collectors.toList());

        properties.add(new Property(GoldenRecordConstants.LINKS_FIELD, mObjectLinks));
        properties.add(new Property(GoldenRecordConstants.RECORD_ID_FIELD, id));

        return new MObject(universeId + "-golden-record", id, properties);
    }

    public static MObject createQuarantineMObject(String universeId, QuarantineEntry entry) {
        List<Property> properties = new ArrayList<>();
        if (entry.getEntity() != null) {
            properties = entry.getEntity().getProperties();
        }

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

        Property propertiesMatched = new Property(FuzzyMatchDetailsConstants.MATCH, new ArrayList<>());
        Property propertiesDuplicated = new Property(FuzzyMatchDetailsConstants.DUPLICATE, new ArrayList<>());
        Property propertiesAlreadyLinked = new Property(FuzzyMatchDetailsConstants.ALREADY_LINKED, new ArrayList<>());

        if ("SUCCESS".equals(result.getStatus())) {
            propertiesMatched = new Property(FuzzyMatchDetailsConstants.MATCH, result.getMatch());
            propertiesDuplicated = new Property(FuzzyMatchDetailsConstants.DUPLICATE, result.getDuplicate());
        } else if ("ALREADY_LINKED".equals(result.getStatus())) {
            MObject alreadyLinkedObject = new MObject(result.getEntity().getDeveloperName(), result.getEntity().getExternalId(), result.getEntity().getProperties());
            propertiesAlreadyLinked = new Property(FuzzyMatchDetailsConstants.ALREADY_LINKED, alreadyLinkedObject);
        }

        List<Property> properties = result.getEntity().getProperties();

        properties.add(new Property(GoldenRecordConstants.SOURCE_ID_FIELD, result.getIdResource()));
        properties.add(new Property(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, (MObject) null));

        properties.add(propertiesMatched);
        properties.add(propertiesDuplicated);
        properties.add(propertiesAlreadyLinked);

        MObject object = new MObject(universeId + "-match", result.getEntity().getExternalId(), properties);
        object.setTypeElementBindingDeveloperName(object.getDeveloperName());

        return object;
    }

    private static MObject createMObjectForLink(GoldenRecord.Link link) {
        List<Property> linkProperties = new ArrayList<>();

        linkProperties.add(new Property("Source", link.getSource()));
        linkProperties.add(new Property("Entity ID", link.getEntityId()));
        linkProperties.add(new Property("Established Date", link.getEstablishedDate()));

        return new MObject(GoldenRecordConstants.LINK, link.getEntityId(), linkProperties);
    }
}
