package com.boomi.flow.services.boomi.mdh.records;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.common.DateFilter;
import com.boomi.flow.services.boomi.mdh.common.Dates;
import com.boomi.flow.services.boomi.mdh.common.ListFilters;
import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import com.manywho.sdk.api.run.elements.type.MObject;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class GoldenRecordRequestBuilder {
    private final ElementIdFinder elementIdFinder;

    @Inject
    public GoldenRecordRequestBuilder(ElementIdFinder elementIdFinder) {
        this.elementIdFinder = elementIdFinder;
    }

    GoldenRecordQueryRequest build(ApplicationConfiguration configuration, String universe, ListFilter filter) {
        GoldenRecordQueryRequest request = new GoldenRecordQueryRequest();
        if (filter != null) {
            if (filter.hasOrderBy()) {
                request.setSort(createRequestOrder(filter));
            }
            if (filter.hasWhere()) {
                request.setFilter(createRequestFilter(configuration, universe, filter));
            }
        }

        return request;
    }

    private GoldenRecordQueryRequest.Sort createRequestOrder(ListFilter filter) {
        GoldenRecordQueryRequest.Sort sort = new GoldenRecordQueryRequest.Sort();
        for (ListFilter.OrderBy orderBy : filter.getOrderBy()) {
            sort.getFields().add(new GoldenRecordQueryRequest.Sort.Field()
                    .setFieldId(orderBy.getColumnName())
                    .setDirection(orderBy.getDirection())
            );
        }
        return sort;
    }

    private GoldenRecordQueryRequest.Filter createRequestFilter(ApplicationConfiguration configuration, String universe, ListFilter filter) {
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
                String operator = mapOperator(field.getCriteriaType(), field.getColumnName(), field.getContentValue());
                fieldFilters.addAll(createFieldsForFilter(configuration, universe, operator, field));
            }
        }

        return queryFilter;
    }

    private String mapOperator(CriteriaType criteriaType, String columnName, String value) {
        String operator = "";

        switch (criteriaType) {
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
                if (value != null && value.toLowerCase().equals("false")) {
                    operator = "IS_NOT_NULL";
                } else {
                    operator = "IS_NULL";
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
                throw new ServiceProblemException(400, "An unsupported criteria type of " + criteriaType + " was given for the column " + columnName);
        }

        return operator;
    }

    private List<GoldenRecordQueryRequest.Filter.FieldValue> createFieldsForFilter(ApplicationConfiguration configuration, String universe, String operator, ListFilterWhere field) {
        if ("IS_NULL".equals(operator) || "IS_NOT_NULL".equals(operator)) {
            // check if this is valid
            return Collections
                    .singletonList(createFieldValue(configuration, universe, field.getColumnName(), null, operator));
        } else if (field.getContentValue() != null) {
            return Collections
                    .singletonList(createFieldValue(configuration, universe, field.getColumnName(), field.getContentValue(), operator));

        } else if (field.getObjectData() != null && field.getObjectData().isEmpty() == false) {
            // mapping field id for the properties of a field group
            return field.getObjectData().stream()
                    .map(MObject::getProperties)
                    .flatMap(Collection::stream)
                    .map(property -> createFieldValue(configuration, universe, property.getDeveloperName(), property.getContentValue(), operator))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private GoldenRecordQueryRequest.Filter.FieldValue createFieldValue(ApplicationConfiguration configuration, String universe, String columnName, String value, String operator) {

        return new GoldenRecordQueryRequest.Filter.FieldValue()
                .setFieldId(elementIdFinder.findIdFromNameOfElement(configuration, universe, columnName))
                .setOperator(operator)
                .setValue(value);
    }

}
