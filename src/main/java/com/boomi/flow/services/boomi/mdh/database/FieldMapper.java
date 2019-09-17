package com.boomi.flow.services.boomi.mdh.database;

import com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetailsConstants;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntryConstants;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.draw.elements.type.TypeElementBinding;
import com.manywho.sdk.api.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.api.draw.elements.type.TypeElementPropertyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class FieldMapper {
    private final static Logger LOGGER = LoggerFactory.getLogger(FieldMapper.class);

    static List<TypeElement> createModelTypes(Universe universe) {
        var modelBasicName = universe.getName();
        var universeName = TypeNameGenerator.createModelName(universe.getName());
        var universeId = universe.getId().toString();
        ArrayList<Universe.Layout.Model.Element> groupFieldElements = new ArrayList<>();

        List<TypeElementProperty> properties = new ArrayList<>();
        List<TypeElementPropertyBinding> propertyBindings = new ArrayList<>();

        // TODO: This doesn't add an ID field... is that a problem? It's only in layout->fields, not layout->model->elements
        for (var element : universe.getLayout().getModel().getElements()) {

            var contentType = fieldTypeToContentType(element.getType(), element.isRepeatable());

            if (contentType == ContentType.List) {
                // TODO: Ignore field groups with repeat flag for now
                continue;
            } else if (contentType == ContentType.Object) {
                groupFieldElements.add(element);
            }

            if (contentType != null) {
                properties.add(new TypeElementProperty(element.getPrettyName(), contentType));
                propertyBindings.add(new TypeElementPropertyBinding(element.getPrettyName(), element.getName()));
            }
        }

        // adding the default properties and bindings for each model type

        // adding properties for golden record
        properties.add(new TypeElementProperty(GoldenRecordConstants.SOURCE_ID, ContentType.String));
        properties.add(new TypeElementProperty(GoldenRecordConstants.CREATED_DATE, ContentType.DateTime));
        properties.add(new TypeElementProperty(GoldenRecordConstants.UPDATED_DATE, ContentType.DateTime));

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
        properties.add(new TypeElementProperty("Duplicate Entities", ContentType.List, modelBasicName));
        properties.add(new TypeElementProperty("Matching Entities", ContentType.List, modelBasicName));
        properties.add(new TypeElementProperty("Already Linked Entities", ContentType.List, modelBasicName));
        properties.add(new TypeElementProperty(GoldenRecordConstants.SOURCE_ID, ContentType.String));

        // adding bindings for Golden Records
        List<TypeElementBinding> bindings = new ArrayList<>();
        var developerSummaryGoldenRecords = "The structure of a golden record for the " + universeName + " universe";
        List<TypeElementPropertyBinding> propertyBindingsGoldenRecord = new ArrayList<>(propertyBindings);
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.SOURCE_ID, GoldenRecordConstants.SOURCE_ID_FIELD));
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.CREATED_DATE, GoldenRecordConstants.CREATED_DATE_FIELD));
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.UPDATED_DATE, GoldenRecordConstants.UPDATED_DATE_FIELD));
        bindings.add(new TypeElementBinding(modelBasicName + " Golden Record", developerSummaryGoldenRecords, universeId + "-golden-record", propertyBindingsGoldenRecord));

        // adding bindings for Quarantine
        var developerSummaryQuarantine = "The structure of a Quarantine " + modelBasicName + " for the " + universeName + " universe";
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
        bindings.add(new TypeElementBinding(modelBasicName + " Quarantine", developerSummaryQuarantine, universeId + "-quarantine", propertyBindingsQuarantine));

        // adding bindings for Matches
        var developerSummaryMatches = "The structure of matches for the " + universeName + " universe";
        List<TypeElementPropertyBinding> propertyBindingsForMatches = new ArrayList<>(propertyBindings);
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.MATCH, FuzzyMatchDetailsConstants.MATCH));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.DUPLICATE, FuzzyMatchDetailsConstants.DUPLICATE));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.ALREADY_LINKED, FuzzyMatchDetailsConstants.ALREADY_LINKED));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(GoldenRecordConstants.SOURCE_ID, GoldenRecordConstants.SOURCE_ID_FIELD));
        bindings.add(new TypeElementBinding(modelBasicName + " Match", developerSummaryMatches, universeId + "-match", propertyBindingsForMatches));

        // create child types
        var types = groupFieldElements.stream()
                .map(element -> createChildTypes(element))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // add model root type
        types.add(new TypeElement(modelBasicName, properties, bindings));

        return types;
    }

    private static List<TypeElement> createChildTypes(Universe.Layout.Model.Element groupFieldElement) {
        List<TypeElement> types = new ArrayList<>();

        List<TypeElementProperty> properties = new ArrayList<>();
        List<TypeElementPropertyBinding> propertyBindings = new ArrayList<>();
        List<TypeElementBinding> bindings = new ArrayList<>();
        var developerSummaryChildProperty = "The structure of a Child Type " + groupFieldElement.getPrettyName();

        for (var element : groupFieldElement.getElements()) {

            var contentType = fieldTypeToContentType(element.getType(), element.isRepeatable());

            if (contentType == ContentType.List) {
                // TODO: Ignore field groups with repeat flag for now
                continue;
            } else if (contentType == ContentType.Object) {
                types.addAll(createChildTypes(element));
            }

            if (contentType != null) {
                properties.add(new TypeElementProperty(element.getPrettyName(), contentType));
                propertyBindings.add(new TypeElementPropertyBinding(element.getPrettyName(), element.getName()));
            }
        }

        bindings.add(new TypeElementBinding(groupFieldElement.getPrettyName() + " Child Type Default",
                developerSummaryChildProperty, groupFieldElement.getName() + "-child", propertyBindings));

        types.add(new TypeElement(groupFieldElement.getName(), properties, bindings));

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
            default:
                LOGGER.warn("Encountered an unsupported element type of {}", type);

                return null;
        }
    }
}
