package com.boomi.flow.services.boomi.mdh.records;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.ListFilters;
import com.google.common.base.Strings;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GoldenRecordRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(GoldenRecordRepository.class);

    private final MdhClient client;

    @Inject
    public GoldenRecordRepository(MdhClient client) {
        this.client = client;
    }

    public List<MObject> create(ApplicationConfiguration configuration, String universe, List<MObject> objects) {
        return null;
    }

    public List<MObject> findAll(ApplicationConfiguration configuration, String universe, ListFilter filter) {
        LOGGER.info("Loading golden records for the universe {} from the Atom at {} with the username {}", universe, configuration.getAtomHostname(), configuration.getAtomUsername());

        var request = new GoldenRecordQueryRequest();

        // TODO: Cleanup everything in this filter block cause it's super ugly
        if (filter != null) {

            if (filter.hasOrderBy()) {
                var sort = new GoldenRecordQueryRequest.Sort();

                for (var orderBy : filter.getOrderBy()) {
                    sort.getFields().add(new GoldenRecordQueryRequest.Sort.Field()
                            .setFieldId(orderBy.getColumnName())
                            .setDirection(orderBy.getDirection())
                    );
                }

                request.setSort(sort);
            }

            if (filter.hasWhere()) {
                var queryFilter = new GoldenRecordQueryRequest.Filter();

                // Created date
                var createdDates = ListFilters.findEnumerableFilters(filter.getWhere(), GoldenRecordConstants.CREATED_DATE_FIELD);
                if (createdDates.isEmpty() == false) {
                    var dateFilter = new GoldenRecordQueryRequest.Filter.DateFilter();

                    createdDates.forEach(createDateFilter(dateFilter));

                    queryFilter.setCreatedDate(dateFilter);
                }

                // Updated date
                var updatedDates = ListFilters.findEnumerableFilters(filter.getWhere(), GoldenRecordConstants.UPDATED_DATE_FIELD);
                if (updatedDates.isEmpty() == false) {
                    var dateFilter = new GoldenRecordQueryRequest.Filter.DateFilter();

                    updatedDates.forEach(createDateFilter(dateFilter));

                    queryFilter.setUpdatedDate(dateFilter);
                }

                // Field values
                var entityFields = filter.getWhere().stream()
                        .sorted(Comparator.comparing(ListFilterWhere::getColumnName))
                        .dropWhile(where -> Arrays.asList(GoldenRecordConstants.CREATED_DATE_FIELD, GoldenRecordConstants.UPDATED_DATE_FIELD).contains(where.getColumnName()))
                        .collect(Collectors.toList());

                if (entityFields.isEmpty() == false) {
                    var fieldFilters = queryFilter.getFieldValues();

                    for (var field : entityFields) {

                        String operator;

                        switch (field.getCriteriaType()) {
                            case Contains:
                                operator = "CONTAINS";

                                break;
                            case EndsWith:
                                operator = "ENDS_WITH";

                                break;
                            case Equal:
                                operator = "EQUALS";

                                break;
                            case GreaterThan:
                                operator = "GREATER_THAN";

                                break;
                            case GreaterThanOrEqual:
                                operator = "GREATER_THAN_EQUAL";

                                break;
                            case IsEmpty:
                                // TODO: Check if this is correct
                                operator = "IS_NULL";

                                break;
                            case LessThan:
                                operator = "LESS_THAN";

                                break;
                            case LessThanOrEqual:
                                operator = "LESS_THAN_EQUAL";

                                break;
                            case NotEqual:
                                operator = "NOT_EQUAL_TO";

                                break;
                            case StartsWith:
                                operator = "STARTS_WITH";

                                break;
                            default:
                                throw new RuntimeException("An unsupported criteria type of " + field.getCriteriaType() + " was given for the column " + field.getColumnName());
                        }

                        fieldFilters.add(new GoldenRecordQueryRequest.Filter.FieldValue()
                                .setFieldId(field.getColumnName())
                                .setOperator(operator)
                                .setValue(field.getContentValue())
                        );
                    }
                }

                request.setFilter(queryFilter);
            }
        }

        var result = client.queryGoldenRecords(configuration.getAtomHostname(), configuration.getAtomUsername(), configuration.getAtomPassword(), universe, request);
        if (result == null || result.getRecords() == null || result.getResultCount() == 0) {
            return new ArrayList<>();
        }

        return result.getRecords().stream()
                .map(entry -> createGoldenRecordObject(universe, entry))
                .collect(Collectors.toList());
    }

    public List<MObject> update(ApplicationConfiguration configuration, String universeId, List<MObject> objects) {
        var universe = client.findUniverse(configuration.getAtomHostname(), configuration.getAtomUsername(), configuration.getAtomPassword(), universeId);

        // TODO: This isn't correct - it would be great to be able to get the actual ID field name (or make a global standard named one)
        String idField = universe.getLayout().getIdXPath()
                .split("/")
                [2];

        for (var object : objects) {
            if (Strings.isNullOrEmpty(object.getExternalId())) {
                // We're creating this object so let's create an ID
                var id = UUID.randomUUID().toString();

                // Set the ID property, so it can be referenced in a Flow
                for (var property : object.getProperties()) {
                    if (property.getDeveloperName().equals(idField)) {
                        property.setContentValue(id);
                    }
                }

                // Set the object's external ID too, which is only used inside Flow itself
                object.setExternalId(id);
            }
        }

        var objectsBySource = objects.stream()
                .collect(Collectors.groupingBy(object -> object.getProperties()
                        .stream()
                        .filter(property -> property.getDeveloperName().equals(GoldenRecordConstants.SOURCE_ID_FIELD))
                        .map(Property::getContentValue)
//                        .filter(source -> source != null && source.isEmpty() == false)
                        .findFirst()
                        .orElseThrow(() -> new ServiceProblemException(400, "No Source ID was given for the record to update"))));

        for (var sourceGroup : objectsBySource.entrySet()) {
            // TODO: Check if we should be setting this to a default value, or error if no source was set
            var sourceId = sourceGroup.getKey().isBlank()
                    ? GoldenRecordConstants.DEFAULT_SOURCE_ID
                    : sourceGroup.getKey();

            var entities = sourceGroup.getValue().stream()
                    .map(entity -> {
                        // Map all the properties to fields, except our "internal" ones
                        var fields = entity.getProperties().stream()
                                .filter(property -> property.getDeveloperName().startsWith("___") == false)
                                .filter(property -> property.getContentValue() != null)
                                .collect(Collectors.toMap(
                                        Property::getDeveloperName,
                                        property -> (Object) property.getContentValue()
                                ));

                        fields.put(idField, entity.getExternalId());

                        return new GoldenRecordUpdateRequest.Entity()
                                .setOp(null)
                                .setName(universe.getLayout().getModel().getName())
                                .setFields(fields);
                    })
                    .collect(Collectors.toList());

            // Now we can save the records into the Hub
            var updateRequest = new GoldenRecordUpdateRequest()
                    .setSource(sourceId)
                    .setEntities(entities);

            client.updateGoldenRecords(
                    configuration.getAtomHostname(),
                    configuration.getAtomUsername(),
                    configuration.getAtomPassword(),
                    universe.getId().toString(),
                    updateRequest
            );
        }


        // NOTE: The endpoint returns a 202, not returning any created objects directly... how will this map?

        return objects;
    }

    private static MObject createGoldenRecordObject(String universe, GoldenRecord record) {
        return createEntityMObject(universe, record.getRecordId(), record.getFields());
    }

    private static Consumer<ListFilterWhere> createDateFilter(GoldenRecordQueryRequest.Filter.DateFilter dateFilter) {
        return where -> {
            var date = OffsetDateTime.parse(where.getContentValue());

            switch (where.getCriteriaType()) {
                case Equal:
                    dateFilter
                            .setFrom(date)
                            .setTo(date);

                    break;

                case GreaterThan:
                case GreaterThanOrEqual:
                    dateFilter.setFrom(date);
                    break;

                case LessThan:
                case LessThanOrEqual:
                    dateFilter.setTo(date);
                    break;

                default:
                    throw new RuntimeException("The criteria type " + where.getCriteriaType() + " is not supported for date fields");
            }
        };
    }

    // TODO: Dedupe this and below
    private static MObject createEntityMObject(String universe, String id, Map<String, Map<String, Object>> entity) {
        if (entity.isEmpty()) {
            return null;
        }

        var entry = entity.entrySet().iterator().next();

        var properties = createPropertiesFromMap(entry.getValue());

        return new MObject(String.format("%s Golden Record", entry.getKey()), id, properties);
    }

    private static List<Property> createPropertiesFromMap(Map<String, Object> map) {
        // We don't really support any nested objects or lists yet, so we filter them out and map to a type property
        return map.entrySet().stream()
                .filter(field -> (field.getValue() instanceof Map) == false)
                .map(field -> new Property(field.getKey(), field.getValue()))
                .collect(Collectors.toList());
    }
}
