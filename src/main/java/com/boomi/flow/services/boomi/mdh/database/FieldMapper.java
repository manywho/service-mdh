package com.boomi.flow.services.boomi.mdh.database;

import com.boomi.flow.services.boomi.mdh.common.Entities;
import com.boomi.flow.services.boomi.mdh.match.FuzzyMatchDetailsConstants;
import com.google.common.base.Strings;
import com.manywho.sdk.api.ContentType;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FieldMapper {
    private final static Logger LOGGER = LoggerFactory.getLogger(FieldMapper.class);

    public enum SearchingBy {
        UNIQUE_ID,
        NAME,
        COLLECTION_TAG
    };


    /**
     * Create a Hashmap that HUB can understand
     */
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
    public static String getEntryNameToSendToHubApi(String modelName, Property property, List<Universe.Layout.Model.Element> elements) {
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
                    property.getContentType() == ContentType.Object ||
                    (property.getObjectData() != null && property.getObjectData().size() > 0)) {

                if (changePropertyName) {
                    newName = universe.getLayout().getModel().getName() + " - " + newName;
                }

                for (MObject childObject:property.getObjectData()) {
                    if (changePropertyName) {
                        childObject.setDeveloperName(newName);
                    }

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
    public static String getUniqueIdByPropertyName(String modelName, Property property, List<Universe.Layout.Model.Element> elements) {
        String fieldName = property.getDeveloperName();

        if (property.getContentType() == ContentType.List) {
            fieldName = Entities.removeModelPrefix(property.getDeveloperName(), modelName);
            Universe.Layout.Model.Element foundElement = findElementBy(elements, fieldName, SearchingBy.COLLECTION_TAG);

            return foundElement.getUniqueId().toLowerCase();
        } else if(property.getContentType() == ContentType.Object) {
            fieldName = Entities.removeModelPrefix(property.getDeveloperName(), modelName);
            Universe.Layout.Model.Element foundElement = findElementBy(elements, fieldName, SearchingBy.NAME);

            return foundElement.getUniqueId().toLowerCase();
        } else if (fieldName.startsWith(modelName + " - ")) {
            // we don't know if it is a list or an object but it has the model name prefix, that means it is a field group
            // it is not possible to have a uniqueid with spaces and "-" in MDH so we are sure that the name was created
            // from a field group from the model "modelName"
            fieldName = Entities.removeModelPrefix(property.getDeveloperName(), modelName);
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
            if (searchingBy == SearchingBy.NAME && element.getName().equalsIgnoreCase(search) ||
                    searchingBy == SearchingBy.COLLECTION_TAG && element.getName().equalsIgnoreCase(search) ||
                    searchingBy == SearchingBy.UNIQUE_ID && element.getUniqueId().equalsIgnoreCase(search)) {
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
            if (property.getContentType() == null) {
                // If we don't know the contentType, then just return the contentValue
                // We can't let the 'switch' deal with this null case, as it would throw a NullPointerException
                return property.getContentValue();
            }

            switch (property.getContentType()) {
                case DateTime:
                    return Strings.isNullOrEmpty(property.getContentValue()) ?
                        // Ignore datetime with empty values
                        null :
                        OffsetDateTime
                            .parse(property.getContentValue())
                            .format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("Z")));
                case Boolean:
                    return property.getContentValue().toLowerCase();
                default:
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
            if (childObject != null) {
                String name = getEntryNameToSendToHubApi(modelName, property, elements);
                mapObject.put(name, childObject);
            }
        }

        Map<String, Object> wrapperObject = new HashMap<>();

        Universe.Layout.Model.Element element = findElementBy(elements, Entities.removeModelPrefix(mObject.getDeveloperName(), modelName), SearchingBy.UNIQUE_ID);

        wrapperObject.put(element.getName(), mapObject);

        return wrapperObject;
    }
}
