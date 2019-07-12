package com.boomi.flow.services.boomi.mdh.database;

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

    static void collectTypes(List<Universe.Layout.Model.Element> elements, String name, String typePrettyName,
                             String universeName, String universeId, List<TypeElement> typeCollected, boolean isModel) {

        List<TypeElementProperty> properties = new ArrayList<>();
        List<TypeElementPropertyBinding> propertyBindings = new ArrayList<>();

        // TODO: This doesn't add an ID field... is that a problem? It's only in layout->fields, not layout->model->elements
        for (var element : elements) {

            var contentType = fieldTypeToContentType(element.getType(), element.isRepeatable());
            if (contentType == null) {
                continue;
            }
            var prettyName = element.getPrettyName();

            if (ContentType.Object.equals(contentType) || ContentType.List.equals(contentType)) {
                prettyName = TypeNameGenerator.createChildTypeName(element.getName(), typePrettyName);
                collectTypes(element.getElements(), element.getName(), prettyName, universeName, universeId, typeCollected,false);
                properties.add(new TypeElementProperty(element.getName(), contentType, element.getName()));
            } else {
                properties.add(new TypeElementProperty(element.getName(), contentType));
            }

            propertyBindings.add(new TypeElementPropertyBinding(element.getName(), element.getName()));
        }

        addPropertiesForGoldenRecord(properties);
        addPropertiesForQuarantine(properties);

        List<TypeElementBinding> bindings = new ArrayList<>();

        if (isModel == false) {
            var developerSummary = "The type " + typePrettyName + " for the " + universeName + " universe";
            bindings.add(new TypeElementBinding(typePrettyName, developerSummary, name, propertyBindings));
        } else {
            addBindingForGoldenRecord(bindings, universeId, universeName, typePrettyName, propertyBindings);
            addBindingForQuarantine(bindings, universeId, universeName, typePrettyName, propertyBindings);
        }

        typeCollected.add(new TypeElement(typePrettyName, properties, bindings));
    }

    private static void addPropertiesForGoldenRecord(List<TypeElementProperty> properties) {
        // add properties for filter by Golden Record
        properties.add(new TypeElementProperty(GoldenRecordConstants.SOURCE_ID, ContentType.String));
        properties.add(new TypeElementProperty(GoldenRecordConstants.CREATED_DATE, ContentType.DateTime));
        properties.add(new TypeElementProperty(GoldenRecordConstants.UPDATED_DATE, ContentType.DateTime));
    }

    private static void addPropertiesForQuarantine(List<TypeElementProperty> properties) {
        //properties.add(new TypeElementProperty(QuarantineEntryConstants.SOURCE_ID, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.SOURCE_ENTITY_ID, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.STATUS, ContentType.String));

        // These properties are all for the response
        //properties.add(new TypeElementProperty(QuarantineEntryConstants.CREATED_DATE, ContentType.DateTime));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.END_DATE, ContentType.DateTime));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.TRANSACTION_ID, ContentType.String));

        properties.add(new TypeElementProperty(QuarantineEntryConstants.CAUSE, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.REASON, ContentType.String));
        properties.add(new TypeElementProperty(QuarantineEntryConstants.RESOLUTION, ContentType.String));
    }

    private static void addBindingForGoldenRecord(List<TypeElementBinding> bindings, String name, String universeName, String typePrettyName, List<TypeElementPropertyBinding> propertyBindings) {
        var developerSummary = "Golden Record " + typePrettyName + " for " + universeName + " universe";
        bindings.add(new TypeElementBinding(typePrettyName + " Golden Record", developerSummary, name + " golden-record", propertyBindings));
    }

    private static void addBindingForQuarantine(List<TypeElementBinding> bindings, String name, String universeName, String typePrettyName, List<TypeElementPropertyBinding> propertyBindings) {
        var developerSummary = "Quarantine " + typePrettyName + " for " + universeName + " universe";
        bindings.add(new TypeElementBinding(typePrettyName + " Quarantine", developerSummary, name + "quarantine", propertyBindings));
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
