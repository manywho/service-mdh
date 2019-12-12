package com.boomi.flow.services.boomi.mdh.database;

import com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetailsConstants;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineEntryConstants;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordConstants;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.google.common.base.Strings;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.draw.elements.type.TypeElementBinding;
import com.manywho.sdk.api.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.api.draw.elements.type.TypeElementPropertyBinding;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FieldMapper {
    private final static Logger LOGGER = LoggerFactory.getLogger(FieldMapper.class);

    static List<TypeElement> createModelTypes(Universe universe) {
        String modelBasicName = universe.getName();
        String universeName = TypeNameGenerator.createModelName(universe.getName());
        String universeId = universe.getId().toString();

        // create child types
        List<TypeElement> types = extractOneLevelChildTypeElements(universe.getLayout().getModel().getElements())
                .stream()
                .map(element -> createChildTypesFromElement(element, modelBasicName))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // create properties and bindings
        List<TypeElementProperty> properties = extractProperties(universe.getLayout().getModel().getElements());
        List<TypeElementPropertyBinding> propertyBindings = extractPropertyBindings(universe.getLayout().getModel().getElements());

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
        properties.add(new TypeElementProperty("Duplicate Entities", ContentType.List, modelBasicName));
        properties.add(new TypeElementProperty("Matching Entities", ContentType.List, modelBasicName));
        properties.add(new TypeElementProperty("Already Linked Entities", ContentType.List, modelBasicName));

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
        bindings.add(new TypeElementBinding(modelBasicName + " Golden Record", developerSummaryGoldenRecords, universeId + "-golden-record", propertyBindingsGoldenRecord));

        // adding bindings for Quarantine
        String developerSummaryQuarantine = "The structure of a Quarantine " + modelBasicName + " for the " + universeName + " universe";
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
        String developerSummaryMatches = "The structure of matches for the " + universeName + " universe";
        List<TypeElementPropertyBinding> propertyBindingsForMatches = new ArrayList<>(propertyBindings);
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS, FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.MATCH, FuzzyMatchDetailsConstants.MATCH));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.DUPLICATE, FuzzyMatchDetailsConstants.DUPLICATE));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(FuzzyMatchDetailsConstants.ALREADY_LINKED, FuzzyMatchDetailsConstants.ALREADY_LINKED));
        propertyBindingsForMatches.add(new TypeElementPropertyBinding(GoldenRecordConstants.SOURCE_ID, GoldenRecordConstants.SOURCE_ID_FIELD));
        bindings.add(new TypeElementBinding(modelBasicName + " Match", developerSummaryMatches, universeId + "-match", propertyBindingsForMatches));

        // add model root type
        types.add(new TypeElement(modelBasicName, properties, bindings));

        return types;
    }

    public static Map<String, Object> createMapFromModelMobject(MObject mObject, Universe universe) {
        Map<String, Object> mapObject = new HashMap<>();

        for (Property property: mObject.getProperties()) {
            if (property.getDeveloperName().startsWith("___")) {
                continue;
            } else {
                Object object = createMapEntry(property, universe.getLayout().getModel().getName(), universe.getLayout().getModel().getElements());

                if (object != null) {
                    if (property.getContentType() != ContentType.Object && property.getContentType() != ContentType.List) {
                        // is not a field group
                        mapObject.put(property.getDeveloperName(), object);
                    } else if (property.getContentType() == ContentType.List) {
                        mapObject.put(getEntryName(property, universe.getLayout().getModel().getElements()), object);
                    } else if (property.getContentType() == ContentType.Object && object instanceof List) {

                        // this is not really a list in hub, we need to do some modifications
                        Map<String, Object> container = (Map<String, Object>) ((List) object).get(0);
                        mapObject.put(property.getDeveloperName(), container.get(property.getDeveloperName()));
                    }
                }
            }
        }

        return mapObject;
    }

    private static String getEntryName(Property property, List<Universe.Layout.Model.Element> elements) {
        Universe.Layout.Model.Element foundElement = elements.stream()
                .filter(element -> element.getName().equals(property.getDeveloperName()))
                . findFirst()
                .orElseGet(Universe.Layout.Model.Element::new);

        if (foundElement.isRepeatable()) {
            return foundElement.getCollectionUniqueId().toLowerCase();
        } else {
            return property.getDeveloperName();
        }
    }

    static Object createMapEntry(Property property, String modelName, List<Universe.Layout.Model.Element> elements) {
        if (property.getContentValue() != null) {
            if (property.getContentType() == ContentType.DateTime && Strings.isNullOrEmpty(property.getContentValue())) {
                // Ignore datetime with empty values
                return null;
            }

            if (property.getContentType() == ContentType.DateTime) {
                return OffsetDateTime
                        .parse(property.getContentValue())
                        .format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("Z")));
            } else {
                return property.getContentValue();
            }
        } else if (property.getObjectData() != null) {
            List<Map<String, Object>> listOfObjects = new ArrayList<>();

            for (MObject mobjectItem: property.getObjectData()) {
                listOfObjects.add(createMapFromMobject(mobjectItem, modelName, elements));
            }

            return listOfObjects;
        }
        return null;
    }

    public static Map<String, Object> createMapFromMobject(MObject mObject, String modelName, List<Universe.Layout.Model.Element> elements) {
        Map<String, Object> mapObject = new HashMap<>();

        for (Property property: mObject.getProperties()) {
            mapObject.put(property.getDeveloperName(), createMapEntry(property, modelName, elements));
        }

        Map<String, Object> wrapperObject = new HashMap<>();

        int removePrefix = (modelName + "-").length();

        wrapperObject.put(mObject.getDeveloperName().substring(removePrefix), mapObject);

        return wrapperObject;
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

    private static List<TypeElementProperty> extractProperties(List<Universe.Layout.Model.Element> elements) {
        List<TypeElementProperty> properties = new ArrayList<>();

        for (Universe.Layout.Model.Element element : elements) {
            ContentType contentType = fieldTypeToContentType(element.getType(), element.isRepeatable());

            if (contentType != null) {
                properties.add(createProperty(element, contentType));
            }
        }

        return properties;
    }

    private static List<TypeElementPropertyBinding> extractPropertyBindings(List<Universe.Layout.Model.Element> elements) {
        List<TypeElementPropertyBinding> propertyBindings = new ArrayList<>();

        for (Universe.Layout.Model.Element element : elements) {
            ContentType contentType = fieldTypeToContentType(element.getType(), element.isRepeatable());

            if (contentType != null) {
                propertyBindings.add(creteTypeElementPropertyBinding(element, contentType));
            }
        }

        return propertyBindings;
    }

    private static TypeElementProperty createProperty(Universe.Layout.Model.Element element, ContentType contentType) {
        String typeElementDeveloperName = null;

        if (contentType == ContentType.Object || contentType == ContentType.List) {
            typeElementDeveloperName = element.getName();
        }

        return new TypeElementProperty(element.getPrettyName(), contentType, typeElementDeveloperName);
    }

    private static TypeElementPropertyBinding creteTypeElementPropertyBinding(Universe.Layout.Model.Element element, ContentType contentType) {
        String typeElementDeveloperName = null;

        if (contentType == ContentType.Object || contentType == ContentType.List) {
            typeElementDeveloperName = element.getName();
        }

        return new TypeElementPropertyBinding(element.getPrettyName(), element.getName(), typeElementDeveloperName);
    }

    private static List<TypeElement> createChildTypesFromElement(Universe.Layout.Model.Element groupFieldElement, String modelBasicName) {
        // create child types
        List<TypeElement> types = extractOneLevelChildTypeElements(groupFieldElement.getElements())
                .stream()
                .map(groupFieldElement1 -> createChildTypesFromElement(groupFieldElement1, modelBasicName))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<TypeElementProperty> properties = extractProperties(groupFieldElement.getElements());
        List<TypeElementPropertyBinding> propertyBindings = extractPropertyBindings(groupFieldElement.getElements());
        List<TypeElementBinding> bindings = new ArrayList<>();

        String developerSummaryChildProperty = "The structure of a child Type " + groupFieldElement.getPrettyName() + " for " + modelBasicName;

        bindings.add(new TypeElementBinding( modelBasicName + " - " + groupFieldElement.getName(),
                developerSummaryChildProperty, modelBasicName + " - " + groupFieldElement.getName(), propertyBindings));

        types.add(new TypeElement(modelBasicName + " - " +groupFieldElement.getName(), "", properties, bindings));

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
