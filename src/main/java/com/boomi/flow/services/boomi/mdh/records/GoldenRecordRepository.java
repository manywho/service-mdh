package com.boomi.flow.services.boomi.mdh.records;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.*;
import com.boomi.flow.services.boomi.mdh.database.FieldMapper;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.boomi.flow.services.boomi.mdh.utilities.OffsetTokenGenerator;
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
    public GoldenRecordRepository(MdhClient client)
    {
        this.client = client;
    }

    public void delete(ApplicationConfiguration configuration, String universeId, List<MObject> objects) {
        update(configuration, objects, universeId, "DELETE");
    }

    public List<MObject> findAll(ApplicationConfiguration configuration, String universeId, ListFilter filter) {
        LOGGER.info("Loading golden records for the universe {} from the Atom at {} with the username {}", universeId, configuration.getHubHostname(), configuration.getHubUsername());
        Universe universe = client.findUniverse(configuration.getHubHostname(), configuration.getHubUsername(), configuration.getHubToken(), universeId);
        GoldenRecordQueryRequest request = new GoldenRecordQueryRequest();

        // TODO: Cleanup everything in this filter block cause it's super ugly
        if (filter != null) {
            GoldenRecordQueryRequest.Sort sort = new GoldenRecordQueryRequest.Sort();

            if (filter.getOrderByPropertyDeveloperName() != null && filter.getOrderByDirectionType() != null) {
                sort.getFields().add(new GoldenRecordQueryRequest.Sort.Field()
                        .setFieldId(filter.getOrderByPropertyDeveloperName().toUpperCase())
                        .setDirection(filter.getOrderByDirectionType().toString())
                );
            }

            if (filter.hasOrderBy()) {
                for (ListFilter.OrderBy orderBy : filter.getOrderBy()) {
                    String currFieldId = orderBy.getColumnName().toUpperCase();
                    // Add if we don't already have a sort rule for the same column / fieldId
                    // based on orderByPropertyDeveloperName
                    boolean isNotDuplicate = !(filter.getOrderByPropertyDeveloperName() != null &&
                            currFieldId.equals(filter.getOrderByPropertyDeveloperName().toUpperCase()));
                    if (isNotDuplicate) {
                        sort.getFields().add(new GoldenRecordQueryRequest.Sort.Field()
                                .setFieldId(currFieldId)
                                .setDirection(orderBy.getDirection())
                        );
                    }
                }
            }
            request.setSort(sort);
            if (filter.getLimit() != null) {
                request.setLimit(filter.getLimit().toString());
            }

            request.setOffsetToken(OffsetTokenGenerator.generate(filter));

            if (filter.hasWhere()) {
                GoldenRecordQueryRequest.Filter queryFilter = new GoldenRecordQueryRequest.Filter();

                // Created date
                List<ListFilterWhere> createdDates = ListFilters.findEnumerableFilters(filter.getWhere(), GoldenRecordConstants.CREATED_DATE_FIELD);
                if (createdDates.isEmpty() == false) {
                    DateFilter dateFilter = new DateFilter();

                    createdDates.forEach(Dates.createDateFilter(dateFilter));

                    queryFilter.setCreatedDate(dateFilter);
                }

                // Updated date
                List<ListFilterWhere> updatedDates = ListFilters.findEnumerableFilters(filter.getWhere(), GoldenRecordConstants.UPDATED_DATE_FIELD);
                if (updatedDates.isEmpty() == false) {
                    DateFilter dateFilter = new DateFilter();

                    updatedDates.forEach(Dates.createDateFilter(dateFilter));

                    queryFilter.setUpdatedDate(dateFilter);
                }

                List<ListFilterWhere> entityFields = filter.getWhere().stream()
                        .sorted(Comparator.comparing(ListFilterWhere::getColumnName))
                        .filter(where -> GoldenRecordConstants.CREATED_DATE_FIELD.equals(where.getColumnName()) == false)
                        .filter(where -> GoldenRecordConstants.UPDATED_DATE_FIELD.equals(where.getColumnName()) == false)
                        .collect(Collectors.toList());

                if (entityFields.isEmpty() == false) {
                    List<GoldenRecordQueryRequest.Filter.FieldValue> fieldFilters = queryFilter.getFieldValues();

                    for (ListFilterWhere field : entityFields) {

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

                                if ("true".equalsIgnoreCase(field.getContentValue())) {
                                    operator = "IS_NULL";
                                } else {
                                    operator = "IS_NOT_NULL";
                                }

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

                        String value = field.getContentValue();
                        if ("IS_NULL".equalsIgnoreCase(operator) || "IS_NOT_NULL".equalsIgnoreCase(operator)) {
                            value = null;
                        }

                        fieldFilters.add(new GoldenRecordQueryRequest.Filter.FieldValue()
                                .setFieldId(field.getColumnName().toUpperCase())
                                .setOperator(operator)
                                .setValue(value)
                        );
                    }
                }

                request.setFilter(queryFilter);
            }
        }

        GoldenRecordQueryResponse result = client.queryGoldenRecords(configuration.getHubHostname(), configuration.getHubUsername(), configuration.getHubToken(), universeId, request);
        if (result == null || result.getRecords() == null || result.getResultCount() == 0) {
            return new ArrayList<>();
        }

        return result.getRecords().stream()
                .map(record -> {
                            MObject mObject = Entities.createGoldenRecordMObject(universeId, record);
                            FieldMapper.renameMobjectPropertiesToUseUniqueId(universe, mObject);

                            return mObject;
                        }
                )
                .collect(Collectors.toList());
    }

    public List<MObject> update(ApplicationConfiguration configuration, String universeId, List<MObject> objects) {
        return update(configuration, objects, universeId, null);
    }

    private String getSourceIdFromProperty(Property property) {
        if (GoldenRecordConstants.SOURCE_ID_FIELD.equals(property.getDeveloperName())) {
            if (property.getContentValue() == null || property.getContentValue().isEmpty()) {
                return GoldenRecordConstants.DEFAULT_SOURCE_ID;
            } else {
                return property.getContentValue();
            }
        }

        throw new RuntimeException("Unexpected error, we can only get source from the source property");
    }

    private List<MObject> update(ApplicationConfiguration configuration, List<MObject> objects, String universeId, String operation) {
        Universe universe = client.findUniverse(configuration.getHubHostname(), configuration.getHubUsername(), configuration.getHubToken(), universeId);

        Map<String, List<MObject>> objectsBySource = objects.stream()
                .map(object -> Entities.setRandomUniqueIdIfEmpty(object, universe.getIdField(), true))
                .collect(Collectors.groupingBy(object -> object.getProperties()
                        .stream()
                        .filter(property -> property.getDeveloperName().equals(GoldenRecordConstants.SOURCE_ID_FIELD))
                        .map(this::getSourceIdFromProperty)
                        .findFirst()
                        .orElseThrow(() -> new ServiceProblemException(400, "No Source ID was given for the record to update"))));

        for (Map.Entry<String, List<MObject>> sourceGroup : objectsBySource.entrySet()) {
            // TODO: Check if we should be setting this to a default value, or error if no source was set
            String sourceId = sourceGroup.getKey();
            List<BatchUpdateRequest.Entity> entities = sourceGroup.getValue().stream()
                    .map(entity -> {
                        // Map all the properties to fields, except our "internal" ones
                        Map<String, Object> fields = FieldMapper.createMapFromModelMobject(universe.getName(), entity, universe);

                        fields.put(universe.getIdField(), Entities.extractFieldIdValueOrRandomGenerate(entity));

                        return new BatchUpdateRequest.Entity()
                                .setOp(operation)
                                .setName(universe.getLayout().getModel().getName())
                                .setFields(fields);
                    })
                    .collect(Collectors.toList());

            // Now we can save the records into the Hub
            BatchUpdateRequest updateRequest = new BatchUpdateRequest()
                    .setSource(sourceId)
                    .setEntities(entities);

            client.updateGoldenRecords(
                    configuration.getHubHostname(),
                    configuration.getHubUsername(),
                    configuration.getHubToken(),
                    universe.getId().toString(),
                    updateRequest
            );
        }

        return objects;
    }
}
