package com.boomi.flow.services.boomi.mdh.database;

import com.boomi.flow.services.boomi.mdh.common.Entities;
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

    public enum SearchingBy {
        UNIQUE_ID,
        NAME,
        COLLECTION_TAG
    };

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

    public static Map<String, Object> createMapFromModelMobject(String modelName, MObject mObject, Universe universe) {
        Map<String, Object> mapObject = new HashMap<>();

        for (Property property: mObject.getProperties()) {
            if (property.getDeveloperName().startsWith("___") || property.getDeveloperName().equals(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS)) {
                continue;
            } else {
                Object object = createMapEntry(property, universe.getLayout().getModel().getName(), universe.getLayout().getModel().getElements());

                if (object == null) {
                    continue;
                }

                String objectName = getEntryNameToSendToHubApi(modelName, property, universe.getLayout().getModel().getElements());
                mapObject.put(objectName, object);
            }
        }

        return mapObject;
    }

    /**
     * from the property name (uniqueId) we need to find the name to be send to hub name/collectionTag
     */
    private static String getEntryNameToSendToHubApi(String modelName, Property property, List<Universe.Layout.Model.Element> elements) {
        String fieldName = property.getDeveloperName();

        if (property.getContentType() == ContentType.Object || property.getContentType() == ContentType.List) {
            fieldName = Entities.removeModelPrefix(property.getDeveloperName(), modelName);
        }

        Universe.Layout.Model.Element foundElement = findElementBy(elements, fieldName, SearchingBy.UNIQUE_ID);

        if (foundElement.isRepeatable()) {
            return foundElement.getCollectionTag();
        }

        return foundElement.getName();
    }

    /**
     * preparing the response to be send to flow
     */
    public static void renameMobjectPropertiesToUseUniqueId(Universe universe, MObject mObject) {
        // we don't need to rename anything related with our properties and neither with Fuzzy Match Details
        for(Property property: mObject.getProperties()) {
            if (property.getDeveloperName().startsWith("__")
                    || property.getDeveloperName().equals(universe.getIdField())
                    || property.getDeveloperName().equals(FuzzyMatchDetailsConstants.FUZZY_MATCH_DETAILS)) {
                continue;
            }

            boolean changePropertyName = true;

            // we only attempt to find a uniqueId if it is not one of these properties
            if (property.getDeveloperName().equals(FuzzyMatchDetailsConstants.ALREADY_LINKED) ||
                    property.getDeveloperName().equals(FuzzyMatchDetailsConstants.MATCH) ||
                    property.getDeveloperName().equals(FuzzyMatchDetailsConstants.DUPLICATE)) {

                changePropertyName = false;
            }
            String newName = property.getDeveloperName();
            if (changePropertyName) {
                newName = getUniqueIdByPropertyName(universe.getLayout().getModel().getName(), property, universe.getLayout().getModel().getElements());
            }

            // if it is one of our properties we don't change the property name, but we still need to change the property names of the object in objectData
            if (property.getContentType() == ContentType.List ||
                    property.getContentType() == ContentType.Object || changePropertyName == false
                    && property.getObjectData() != null) {

                if (changePropertyName) {
                    newName = universe.getLayout().getModel().getName() + " - " + newName;
                }
                for (MObject childObject:property.getObjectData()) {
                    childObject.setDeveloperName(newName);
                    renameMobjectPropertiesToUseUniqueId(universe, childObject);
                }
            }

            property.setDeveloperName(newName);
        }
    }

    /**
     * when the response is translated automatically mapped to MObject the names are still name/collectionTag,
     * we search in this method for a name that flow understand (uniqueId)
     */
    private static String getUniqueIdByPropertyName(String modelName, Property property, List<Universe.Layout.Model.Element> elements) {
        String fieldName = property.getDeveloperName();

        if (property.getContentType() == ContentType.List) {
            fieldName = Entities.removeModelPrefix(property.getDeveloperName(), modelName);
            Universe.Layout.Model.Element foundElement = findElementBy(elements, fieldName, SearchingBy.COLLECTION_TAG);

            return foundElement.getUniqueId().toLowerCase();
        } else if(property.getContentType() == ContentType.Object) {
            fieldName = Entities.removeModelPrefix(property.getDeveloperName(), modelName);
            Universe.Layout.Model.Element foundElement = findElementBy(elements, fieldName, SearchingBy.NAME);

            return foundElement.getUniqueId().toLowerCase();
        }

        Universe.Layout.Model.Element foundElement = findElementBy(elements, fieldName, SearchingBy.NAME);

        if (foundElement == null) {
            throw new RuntimeException("The field "+ fieldName + " hasn't been found, please update the service");
        }

        return foundElement.getUniqueId().toLowerCase();
    }

    /*
        this is a recursive search through all the element, we can search elements by collectionTag, Name or UniqueID
     */
    static Universe.Layout.Model.Element findElementBy(List<Universe.Layout.Model.Element> elements, String search, SearchingBy searchingBy) {
        Universe.Layout.Model.Element found = null;
        for(Universe.Layout.Model.Element element: elements) {
            if (searchingBy == SearchingBy.NAME && element.getName().toLowerCase().equals(search) ||
                    searchingBy == SearchingBy.COLLECTION_TAG && element.getName().toLowerCase().equals(search) ||
                    searchingBy == SearchingBy.UNIQUE_ID && element.getUniqueId().toLowerCase().equals(search)) {
                return element;
            }

            if (element.getElements() != null && element.getElements().size() > 0) {
                found = findElementBy(element.getElements(), search, searchingBy);

                if (found != null) {
                    return found;
                }
            }


        }

        return found;
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
            if (property.getContentType() == ContentType.Object) {
                Map<String, Object> objectHashMap = new HashMap<>();
                if (property.getObjectData().size() > 0) {
                    MObject firstAndUniqueObject = property.getObjectData().get(0);
                    objectHashMap = createMapFromMobject(firstAndUniqueObject, modelName, elements);

                    return objectHashMap.values().iterator().next();
                }

                return null;
            }

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
            Object childObject = createMapEntry(property, modelName, elements);

            String name = getEntryNameToSendToHubApi(modelName, property, elements);
            mapObject.put(name, childObject);
        }

        Map<String, Object> wrapperObject = new HashMap<>();

        Universe.Layout.Model.Element element = findElementBy(elements, Entities.removeModelPrefix(mObject.getDeveloperName(), modelName), SearchingBy.UNIQUE_ID);

        wrapperObject.put(element.getName(), mapObject);

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
            databaseFieldName = modelName + " - " + fieldName;
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
