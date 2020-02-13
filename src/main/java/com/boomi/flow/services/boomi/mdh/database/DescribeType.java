package com.boomi.flow.services.boomi.mdh.database;

import com.boomi.flow.services.boomi.mdh.common.Entities;
import com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetailsConstants;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntryConstants;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.draw.elements.type.TypeElementBinding;
import com.manywho.sdk.api.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.api.draw.elements.type.TypeElementPropertyBinding;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntryConstants;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DescribeType {
    private final static Logger LOGGER = LoggerFactory.getLogger(DescribeType.class);

    static List<TypeElement> createModelTypes(Universe universe) {
        String modelName = universe.getName();
        String universeName = TypeNameGenerator.createModelName(universe.getName());
        String universeId = universe.getId().toString();

        // create child types
        List<TypeElement> types = extractOneLevelChildTypeElements(universe.getLayout().getModel().getElements())
                .stream()
                .map(element -> createChildTypesFromElement(element, modelName))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // create properties and bindings
        List<TypeElementProperty> properties = extractProperties(universe.getLayout().getModel().getName(), universe.getLayout().getModel().getElements());
        List<TypeElementPropertyBinding> propertyBindings = extractPropertyBindings(modelName, universe.getLayout().getModel().getElements());

        // adding the default properties and bindings for each model type

        // adding properties for golden record
        properties.add(new TypeElementProperty(GoldenRecordConstants.SOURCE_ID, ContentType.String));
        properties.add(new TypeElementProperty(GoldenRecordConstants.CREATED_DATE, ContentType.DateTime));
        properties.add(new TypeElementProperty(GoldenRecordConstants.UPDATED_DATE, ContentType.DateTime));
        properties.add(new TypeElementProperty(GoldenRecordConstants.RECORD_ID, ContentType.String));
        properties.add(new TypeElementProperty(GoldenRecordConstants.ENTITY_ID, ContentType.String));
        properties.add(new TypeElementProperty(GoldenRecordConstants.LINKS, ContentType.List, GoldenRecordConstants.LINK));

        // adding properties for quarantine
        properties.add(new TypeElementProperty(QuarantineEntryConstants.SOURCE_ID, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.CREATED_DATE, ContentType.DateTime));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.SOURCE_ENTITY_ID, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.STATUS, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.END_DATE, ContentType.DateTime));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.TRANSACTION_ID, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.CAUSE, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.REASON, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.RESOLUTION, ContentType.String));

        // adding properties for match entities
        properties.add(new TypeElementProperty("Fuzzy Match Details", ContentType.Object, "Fuzzy Match Details"));
        properties.add(new TypeElementProperty("Duplicate Entities", ContentType.List, modelName));
        properties.add(new TypeElementProperty("Matching Entities", ContentType.List, modelName));
        properties.add(new TypeElementProperty("Already Linked Entities", ContentType.List, modelName));

        // adding bindings for Golden Records
        List<TypeElementBinding> bindings = new ArrayList<>();
        String developerSummaryGoldenRecords = "The structure of a golden record for the " + universeName + " universe";
        List<TypeElementPropertyBinding> propertyBindingsGoldenRecord = new ArrayList<>(propertyBindings);
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.SOURCE_ID, GoldenRecordConstants.SOURCE_ID_FIELD));
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.CREATED_DATE, GoldenRecordConstants.CREATED_DATE_FIELD));
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.UPDATED_DATE, GoldenRecordConstants.UPDATED_DATE_FIELD));
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.RECORD_ID, GoldenRecordConstants.RECORD_ID_FIELD));
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.ENTITY_ID, GoldenRecordConstants.ENTITY_ID_FIELD));
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.LINKS, GoldenRecordConstants.LINKS_FIELD));
        bindings.add(new TypeElementBinding(modelName + " Golden Record", developerSummaryGoldenRecords, universeId + "-golden-record", propertyBindingsGoldenRecord));

        // adding bindings for Quarantine
        String developerSummaryQuarantine = "The structure of a Quarantine " + modelName + " for the " + universeName + " universe";
        List<TypeElementPropertyBinding> propertyBindingsQuarantine = new ArrayList<>(propertyBindings);
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.STATUS, QuarantineEntryConstants.STATUS_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.SOURCE_ID, QuarantineEntryConstants.SOURCE_ID_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.SOURCE_ENTITY_ID, QuarantineEntryConstants.SOURCE_ENTITY_ID_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.CREATED_DATE, QuarantineEntryConstants.CREATED_DATE_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.END_DATE, QuarantineEntryConstants.END_DATE_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.TRANSACTION_ID, QuarantineEntryConstants.TRANSACTION_ID_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.CAUSE, QuarantineEntryConstants.CAUSE_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.REASON, QuarantineEntryConstants.REASON_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.RESOLUTION, QuarantineEntryConstants.RESOLUTION_FIELD));
        bindings.add(new TypeElementBinding(modelName + " Quarantine", developerSummaryQuarantine, universeId + "-quarantine", propertyBindingsQuarantine));

        // adding bindings for Matches
        String developerSummaryMatches = "The structure of matches for the " + universeName + " universe";
        List<TypeElementPropertyBinding> propertyBindingsForMatches = new ArrayList<>(propertyBindings);
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.MATCH, FuzzyMatchDetailsConstants.MATCH));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.DUPLICATE, FuzzyMatchDetailsConstants.DUPLICATE));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.ALREADY_LINKED, FuzzyMatchDetailsConstants.ALREADY_LINKED));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(GoldenRecordConstants.SOURCE_ID, GoldenRecordConstants.SOURCE_ID_FIELD));
        bindings.add(new TypeElementBinding(modelName + " Match", developerSummaryMatches, universeId + "-match", propertyBindingsForMatches));

        // add model root type
        types.add(new TypeElement(modelName, properties, bindings));

        return types;
    }

    private static List<Universe.Layout.Model.Element> extractOneLevelChildTypeElements(List<Universe.Layout.Model.Element> elements) {
        List<Universe.Layout.Model.Element> childTypeElement= new ArrayList<>();

        for (Universe.Layout.Model.Element element : elements) {
            ContentType contentType = fieldTypeToContentType(element.getType(), element.isRepeatable());

            if (contentType == ContentType.Object || contentType == ContentType.List) {
                childTypeElement.add(element);
            }
        }

        return childTypeElement;
    }

    private static List<TypeElementProperty> extractProperties(String modelName, List<Universe.Layout.Model.Element> elements) {
        List<TypeElementProperty> properties = new ArrayList<>();

        for (Universe.Layout.Model.Element element : elements) {
            ContentType contentType = fieldTypeToContentType(element.getType(), element.isRepeatable());

            if (contentType != null) {
                properties.add(createProperty(element, modelName, contentType));
            }
        }

        return properties;
    }

    private static List<TypeElementPropertyBinding> extractPropertyBindings(String modelName, List<Universe.Layout.Model.Element> elements) {
        List<TypeElementPropertyBinding> propertyBindings = new ArrayList<>();

        for (Universe.Layout.Model.Element element : elements) {
            ContentType contentType = fieldTypeToContentType(element.getType(), element.isRepeatable());

            if (contentType != null) {
                propertyBindings.add(creteTypeElementPropertyBinding(modelName, element, contentType));
            }
        }

        return propertyBindings;
    }

    private static TypeElementProperty createProperty(Universe.Layout.Model.Element element, String modelName, ContentType contentType) {
        String typeElementDeveloperName = null;

        if (contentType == ContentType.Object || contentType == ContentType.List) {
            typeElementDeveloperName = Entities.addingModelPrefix(modelName, element.getUniqueId().toLowerCase());
        }

        return new TypeElementProperty(element.getUniqueId().toLowerCase(), contentType, typeElementDeveloperName);
    }

    private static TypeElementPropertyBinding creteTypeElementPropertyBinding(String modelName, Universe.Layout.Model.Element element, ContentType contentType) {
        String typeElementDeveloperName = null;
        String fieldName = element.getUniqueId().toLowerCase();
        String databaseFieldName = fieldName;

        if (contentType == ContentType.Object || contentType == ContentType.List) {
            typeElementDeveloperName = element.getName();
            databaseFieldName = Entities.addingModelPrefix(modelName, fieldName);
        }

        return new TypeElementPropertyBinding(fieldName, databaseFieldName, typeElementDeveloperName);
    }

    private static List<TypeElement> createChildTypesFromElement(Universe.Layout.Model.Element groupFieldElement, String modelName) {
        // create child types
        List<TypeElement> types = extractOneLevelChildTypeElements(groupFieldElement.getElements())
                .stream()
                .map(groupFieldElement1 -> createChildTypesFromElement(groupFieldElement1, modelName))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<TypeElementProperty> properties = extractProperties(modelName, groupFieldElement.getElements());
        List<TypeElementPropertyBinding> propertyBindings = extractPropertyBindings(modelName, groupFieldElement.getElements());
        List<TypeElementBinding> bindings = new ArrayList<>();

        String developerSummaryChildProperty = "The structure of a child Type " + groupFieldElement.getPrettyName() + " for " + modelName;

        bindings.add(new TypeElementBinding( Entities.addingModelPrefix(modelName, groupFieldElement.getUniqueId().toLowerCase()),
                developerSummaryChildProperty, Entities.addingModelPrefix(modelName, groupFieldElement.getUniqueId().toLowerCase()), propertyBindings));

        types.add(new TypeElement(Entities.addingModelPrefix(modelName, groupFieldElement.getUniqueId().toLowerCase()), "", properties, bindings));

        return types;
    }

    private static ContentType fieldTypeToContentType(String type, boolean repeatable) {
        if (repeatable && "CONTAINER".equals(type)) {
            return ContentType.List;
        }

        switch (type) {
            case "STRING":
                return ContentType.String;
            case "INTEGER":
                return ContentType.Number;
            case "FLOAT":
                return ContentType.Number;
            case "DATETIME":
            case "DATE":
                return ContentType.DateTime;
            case "BOOLEAN":
                return ContentType.Boolean;
            case "CONTAINER":
                return ContentType.Object;
            case "ENUMERATION":
                return ContentType.String;
            case "REFERENCE":
                return ContentType.String;
            case "CLOB":
                return ContentType.String;
            case "TIME":
                return ContentType.String;
            default:
                LOGGER.warn("Encountered an unsupported element type of {}", type);

                return null;
        }
    }

}
