package com.boomi.flow.services.boomi.mdh.common;

import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.ListFilterWhere;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

public class Dates {
    public static Consumer<ListFilterWhere> createDateFilter(DateFilter dateFilter) {
        return where -> {
            OffsetDateTime date = OffsetDateTime.parse(where.getContentValue());

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
                    throw new ServiceProblemException(400, "The criteria type " + where.getCriteriaType() + " is not supported for date fields");
            }
        };
    }
}
