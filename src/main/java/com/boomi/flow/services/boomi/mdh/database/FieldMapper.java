package com.boomi.flow.services.boomi.mdh.database;

import com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetialsConstants;
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
import java.util.List;

class FieldMapper {
    private final static Logger LOGGER = LoggerFactory.getLogger(FieldMapper.class);

    static void collectTypes(List<Universe.Layout.Model.Element> elements,
                             String name, String typePrettyName, String universeName, String universeId,
                             List<TypeElement> typeCollected, boolean isModel) {

        List<TypeElementProperty> properties = new ArrayList<>();
        List<TypeElementPropertyBinding> propertyBindings = new ArrayList<>();

        // TODO: This doesn't add an ID field... is that a problem? It's only in layout->fields, not layout->model->elements
        for (var element : elements) {

            var contentType = fieldTypeToContentType(element.getType(), element.isRepeatable());
            // TODO: Ignore field groups (child types) until the bindings for child Types are supported in engine
            var isChildType = ContentType.Object.equals(contentType) || ContentType.List.equals(contentType);

            if (contentType == null || isChildType) {
                continue;
            }

            if (ContentType.Object.equals(contentType) || ContentType.List.equals(contentType)) {
                collectTypes(element.getElements(), element.getName(), element.getPrettyName(), universeName, universeId, typeCollected,false);
                properties.add(new TypeElementProperty(element.getPrettyName(), contentType, element.getPrettyName()));
                propertyBindings.add(new TypeElementPropertyBinding(element.getPrettyName(), element.getName()));
            } else {
                properties.add(new TypeElementProperty(element.getPrettyName(), contentType));
                propertyBindings.add(new TypeElementPropertyBinding(element.getPrettyName(), element.getName()));
            }
        }

        // common for golden Records and Quarantine
        properties.add(new TypeElementProperty(GoldenRecordConstants.SOURCE_ID, ContentType.String));
        properties.add(new TypeElementProperty(GoldenRecordConstants.CREATED_DATE, ContentType.DateTime));

        addPropertiesForGoldenRecord(properties);
        addPropertiesForQuarantine(properties);
        addPropertiesForMatchEntities(typePrettyName, properties);

        List<TypeElementBinding> bindings = new ArrayList<>();

        if (isModel == false) {
            var developerSummary = "The type " + typePrettyName + " for the " + universeName + " universe";
            bindings.add(new TypeElementBinding(typePrettyName, developerSummary, name, propertyBindings));
        } else {
            addBindingForGoldenRecord(bindings, universeId, universeName, typePrettyName, propertyBindings);
            addBindingForQuarantine(bindings, universeId, universeName, typePrettyName, propertyBindings);
            addBindingForMatches(bindings, universeId, universeName, typePrettyName, propertyBindings);
        }

        typeCollected.add(new TypeElement(typePrettyName, properties, bindings));
    }

    private static void addPropertiesForMatchEntities(String name, List<TypeElementProperty> properties) {
        properties.add(new TypeElementProperty("Fuzzy Match Details", ContentType.Object, "Fuzzy Match Details"));
        properties.add(new TypeElementProperty("Duplicated Entities", ContentType.List, name));
        properties.add(new TypeElementProperty("Matched Entities", ContentType.List, name));
        properties.add(new TypeElementProperty("Already Linked Entities", ContentType.List, name));
    }

    private static void addPropertiesForGoldenRecord(List<TypeElementProperty> properties) {
        // add properties for filter by Golden Record
        properties.add(new TypeElementProperty(GoldenRecordConstants.UPDATED_DATE, ContentType.DateTime));
    }

    private static void addPropertiesForQuarantine(List<TypeElementProperty> properties) {
        properties.add(new TypeElementProperty(QuarantineEntryConstants.SOURCE_ENTITY_ID, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.STATUS, ContentType.String));

        // These properties are all for the response
        properties.add(new TypeElementProperty(QuarantineEntryConstants.END_DATE, ContentType.DateTime));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.TRANSACTION_ID, ContentType.String));

        properties.add(new TypeElementProperty(QuarantineEntryConstants.CAUSE, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.REASON, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.RESOLUTION, ContentType.String));
    }

    private static void addBindingForGoldenRecord(List<TypeElementBinding> bindings, String name, String universeName, String typePrettyName, List<TypeElementPropertyBinding> propertyBindings) {
        var developerSummary = "The structure of a golden record for the " + universeName + " universe";

        List<TypeElementPropertyBinding> propertyBindingsGoldenRecord = new ArrayList<>(propertyBindings);

        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.SOURCE_ID, GoldenRecordConstants.SOURCE_ID_FIELD));
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.CREATED_DATE, GoldenRecordConstants.CREATED_DATE_FIELD));
        propertyBindingsGoldenRecord.add(new TypeElementPropertyBinding(GoldenRecordConstants.UPDATED_DATE, GoldenRecordConstants.UPDATED_DATE_FIELD));

        bindings.add(new TypeElementBinding(typePrettyName + " Golden Record", developerSummary, name + " golden-record", propertyBindingsGoldenRecord));
    }

    private static void addBindingForMatches(List<TypeElementBinding> bindings, String name, String universeName, String typePrettyName, List<TypeElementPropertyBinding> propertyBindings) {
        var developerSummary = "The structure of matches for the " + universeName + " universe";

        List<TypeElementPropertyBinding> propertyBindingsForMatches = new ArrayList<>(propertyBindings);

        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS, FuzzyMatchDetialsConstants.FUZZY_MATCH_DETAILS));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetialsConstants.MATCH, FuzzyMatchDetialsConstants.MATCH_FIELD));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetialsConstants.DUPLICATE, FuzzyMatchDetialsConstants.DUPLICATE_FIELD));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetialsConstants.ALREADY_LINKED, FuzzyMatchDetialsConstants.ALREADY_LINKED_FIELD));
        var developerName = typePrettyName + " Match";
        var databaseTableName = name + " match";

        bindings.add(new TypeElementBinding(developerName, developerSummary, databaseTableName, propertyBindingsForMatches));
    }

    private static void addBindingForQuarantine(List<TypeElementBinding> bindings, String name, String universeName, String typePrettyName, List<TypeElementPropertyBinding> propertyBindings) {
        var developerSummary = "The structure of a Quarantine " + typePrettyName + " for the " + universeName + " universe";

        List<TypeElementPropertyBinding> propertyBindingsQuarantine = new ArrayList<>(propertyBindings);

        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.STATUS, QuarantineEntryConstants.STATUS_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(GoldenRecordConstants.SOURCE_ID, GoldenRecordConstants.SOURCE_ID_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.SOURCE_ENTITY_ID, QuarantineEntryConstants.SOURCE_ENTITY_ID_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(GoldenRecordConstants.CREATED_DATE, GoldenRecordConstants.CREATED_DATE_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.END_DATE, QuarantineEntryConstants.END_DATE_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.TRANSACTION_ID, QuarantineEntryConstants.TRANSACTION_ID_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.CAUSE, QuarantineEntryConstants.CAUSE_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.REASON, QuarantineEntryConstants.REASON_FIELD));
        propertyBindingsQuarantine.add(new TypeElementPropertyBinding(QuarantineEntryConstants.RESOLUTION, QuarantineEntryConstants.RESOLUTION_FIELD));

        bindings.add(new TypeElementBinding(typePrettyName + " Quarantine", developerSummary, name + " quarantine", propertyBindingsQuarantine));
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
