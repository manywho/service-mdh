package com.boomi.flow.services.boomi.mdh.common;

import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListFilters {
    public static Optional<String> findFilterValue(@NotNull List<ListFilterWhere> wheres, String column, CriteriaType criteriaType) {
        return wheres.stream()
                .filter(where -> where.getColumnName().equals(column))
                .filter(where -> where.getCriteriaType().equals(criteriaType))
                .findFirst()
                .map(ListFilterWhere::getContentValue);
    }

    public static List<ListFilterWhere> findEnumerableFilters(@NotNull List<ListFilterWhere> wheres, String field) {
        return wheres.stream()
                .filter(where -> where.getColumnName().equals(field))
                .collect(Collectors.toList());
    }

    public static List<String> findEnumerableFilterValues(@NotNull List<ListFilterWhere> wheres, String field, String... validValues) {
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

    public static Optional<String> findConstrainedFilter(@NotNull List<ListFilterWhere> wheres, String field, String... validValues) {
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
            throw new ServiceProblemException(400, "An unsupported criteria type was given in a filter for the field '" + field + "'");
        }

        List<String> values = Arrays.asList(validValues);

        if (filters.stream().noneMatch(where -> values.contains(where.getContentValue()))) {
            throw new ServiceProblemException(400, "The value of the " + field + " filter must be one of " + String.join(", ", values));
        }
    }
}
