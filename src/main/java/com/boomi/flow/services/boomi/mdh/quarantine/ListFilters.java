package com.boomi.flow.services.boomi.mdh.quarantine;

import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListFilters {
    public static Optional<String> getWhereValue(List<ListFilterWhere> wheres, String column, CriteriaType criteriaType) {
        return wheres.stream()
                .filter(where -> where.getColumnName().equals(column))
                .filter(where -> where.getCriteriaType().equals(criteriaType))
                .findFirst()
                .map(ListFilterWhere::getContentValue);
    }

    public static List<String> getWhereValues(List<ListFilterWhere> wheres, String column, CriteriaType criteriaType) {
        return wheres.stream()
                .filter(where -> where.getColumnName().equals(column))
                .filter(where -> where.getCriteriaType().equals(criteriaType))
                .map(ListFilterWhere::getContentValue)
                .collect(Collectors.toList());
    }

    public static List<String> findEnumerableFilter(List<ListFilterWhere> wheres, String field, String... validValues) {
        List<ListFilterWhere> filters = wheres.stream()
                .filter(where -> where.getColumnName().equals(field))
                .collect(Collectors.toList());

        if (filters.isEmpty()) {
            return new ArrayList<>();
        }

        validateField(field, filters, validValues);

        return filters.stream()
                .map(ListFilterWhere::getContentValue)
                .collect(Collectors.toList());
    }

    public static Optional<String> findConstrainedFilter(List<ListFilterWhere> wheres, String field, String... validValues) {
        List<ListFilterWhere> filters = wheres.stream()
                .filter(where -> where.getColumnName().equals(field))
                .collect(Collectors.toList());

        if (filters.isEmpty()) {
            return Optional.empty();
        }

        validateField(field, filters, validValues);

        return filters.stream()
                .findFirst()
                .map(ListFilterWhere::getContentValue);
    }

    private static void validateField(String field, List<ListFilterWhere> filters, String... validValues) {
        if (filters.stream().noneMatch(where -> where.getCriteriaType().equals(CriteriaType.Equal))) {
            throw new RuntimeException("An unsupported criteria type was given in a filter for the field '" + field + "'");
        }

        var values = Arrays.asList(validValues);

        if (filters.stream().noneMatch(where -> values.contains(where.getContentValue()))) {
            throw new RuntimeException("The value of the " + field + " filter must be one of " + String.join(", ", values));
        }
    }
}
