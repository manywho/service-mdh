package com.boomi.flow.services.boomi.mdh.records;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.*;
import com.google.common.base.Strings;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class GoldenRecordRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(GoldenRecordRepository.class);

    private final MdhClient client;

    @Inject
    public GoldenRecordRepository(MdhClient client) {
        this.client = client;
    }

    public void delete(ApplicationConfiguration configuration, String universeId, List<MObject> objects) {
        update(configuration, objects, universeId, "DELETE");
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
                    var dateFilter = new DateFilter();

                    createdDates.forEach(Dates.createDateFilter(dateFilter));

                    queryFilter.setCreatedDate(dateFilter);
                }

                // Updated date
                var updatedDates = ListFilters.findEnumerableFilters(filter.getWhere(), GoldenRecordConstants.UPDATED_DATE_FIELD);
                if (updatedDates.isEmpty() == false) {
                    var dateFilter = new DateFilter();

                    updatedDates.forEach(Dates.createDateFilter(dateFilter));

                    queryFilter.setUpdatedDate(dateFilter);
                }

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
                                throw new ServiceProblemException(400, "An unsupported criteria type of " + field.getCriteriaType() + " was given for the column " + field.getColumnName());
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
                .map(record -> Entities.createGoldenRecordMObject(universe, record.getRecordId(), record.getFields()))
                .collect(Collectors.toList());
    }

    public List<MObject> update(ApplicationConfiguration configuration, String universeId, List<MObject> objects) {
        return update(configuration, objects, universeId, null);
    }

    private List<MObject> update(ApplicationConfiguration configuration, List<MObject> objects, String universeId, String operation) {
        var universe = client.findUniverse(configuration.getAtomHostname(), configuration.getAtomUsername(), configuration.getAtomPassword(), universeId);

        objects.stream()
                .filter(object -> Strings.isNullOrEmpty(object.getExternalId()))
                .forEach(object -> Entities.addRandomUniqueId(object, universe.getIdField()));

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

                        fields.put(universe.getIdField(), entity.getExternalId());

                        return new BatchUpdateRequest.Entity()
                                .setOp(operation)
                                .setName(universe.getLayout().getModel().getName())
                                .setFields(fields);
                    })
                    .collect(Collectors.toList());

            // Now we can save the records into the Hub
            var updateRequest = new BatchUpdateRequest()
                    .setSource(sourceId)
                    .setEntities(entities);

            // NOTE: The endpoint returns a 202, not returning any created objects directly... how will this map? Do we care about creating golden records?
            client.updateGoldenRecords(
                    configuration.getAtomHostname(),
                    configuration.getAtomUsername(),
                    configuration.getAtomPassword(),
                    universe.getId().toString(),
                    updateRequest
            );
        }

        return objects;
    }
}
