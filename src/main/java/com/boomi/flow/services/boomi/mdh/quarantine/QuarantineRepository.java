package com.boomi.flow.services.boomi.mdh.quarantine;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.DateFilter;
import com.boomi.flow.services.boomi.mdh.common.Dates;
import com.boomi.flow.services.boomi.mdh.common.Entities;
import com.boomi.flow.services.boomi.mdh.common.ListFilters;
import com.boomi.flow.services.boomi.mdh.database.FieldMapper;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.boomi.flow.services.boomi.mdh.universes.UniverseRepository;
import com.boomi.flow.services.boomi.mdh.utilities.OffsetTokenGenerator;
import com.manywho.sdk.api.ComparisonType;
import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuarantineRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(UniverseRepository.class);

    private final static String[] VALID_CAUSES = new String[] {
            "AMBIGUOUS_MATCH",
            "DUPLICATE_KEY",
            "ENRICH_ERROR",
            "FIELD_FORMAT_ERROR",
            "INCORPORATE_ERROR",
            "MATCH_REFERENCE_UNKNOWN",
            "MULTIPLE_MATCHES",
            "PARSE_FAILURE",
            "POSSIBLE_DUPLICATE",
            "REFERENCE_UNKNOWN",
            "REQUIRED_FIELD",
            "REQUIRES_APPROVAL",
            "REQUIRES_END_DATE_APPROVAL",
            "REQUIRES_UPDATE_APPROVAL"
    };

    private final static String[] VALID_RESOLUTIONS = new String[] {
            "GRID_DELETED",
            "INCORPORATE_SUCCESS",
            "SUPERSEDED",
            "USER_APPROVED",
            "USER_IGNORE",
            "USER_IGNORED_ENRICHMENT",
            "USER_MATCHED",
            "USER_REJECTED",
            "USER_REPLAY",
            "USER_REPLAY_WITH_EDITS",
            "USER_RETRIED_ENRICHMENT",
            "USER_SELECTIVE_MERGED"
    };

    private final static String[] VALID_STATUSES = new String[] {
            "ALL",
            "ACTIVE",
            "RESOLVED"
    };

    private final MdhClient mdhClient;

    @Inject
    public QuarantineRepository(MdhClient mdhClient) {
        this.mdhClient = mdhClient;
    }

    public List<MObject> findAll(ApplicationConfiguration configuration, String universeId, ListFilter filter) {
        LOGGER.info("Loading quarantine entries for the universe {} from the Atom at {} with the username {}", universeId, configuration.getHubHostname(), configuration.getHubUsername());

        Integer limit = (filter == null) ? null : filter.getLimit();

        QuarantineQueryRequest queryRequest = new QuarantineQueryRequest()
                .setLimit(limit)
                .setOffsetToken(OffsetTokenGenerator.generate(filter))
                .setFilter(mapToQueryFilter(filter))
                .setIncludeData(true);

        queryRequest.setType(findStatusFilter(filter));

        Universe universe = mdhClient.findUniverse(configuration.getHubHostname(), configuration.getHubUsername(), configuration.getHubToken(), universeId);

        QuarantineQueryResponse result = mdhClient.queryQuarantineEntries(configuration.getHubHostname(), configuration.getHubUsername(), configuration.getHubToken(), universeId, queryRequest);
        if (result == null || result.getEntries() == null) {
            return new ArrayList<>();
        }

        return result.getEntries().stream()
                .map(entry -> {
                    MObject mObject = Entities.createQuarantineMObject(universeId, entry);
                    FieldMapper.renameMobjectPropertiesToUseUniqueId(universe, mObject);

                    return mObject;
                })
                .collect(Collectors.toList());
    }

    private String findStatusFilter(ListFilter filter) {
        if (filter != null && filter.hasWhere()) {
            // Status
            return ListFilters.findConstrainedFilter(filter.getWhere(), QuarantineEntryConstants.STATUS_FIELD, VALID_STATUSES)
                    .orElse(null);
        }

        return null;
    }

    private  QuarantineQueryRequest.Filter mapToQueryFilter(ListFilter filter) {
        QuarantineQueryRequest.Filter queryFilter = new QuarantineQueryRequest.Filter();

        if (filter != null) {
            if (filter.getLimit() > 200) {
                LOGGER.warn("An unsupported limit of {} was given", filter.getLimit());

                throw new ServiceProblemException(400, "MDH does not support a limit greater than 200");
            }

            if (filter.getComparisonType() != null && filter.getComparisonType().equals(ComparisonType.Or)) {
                LOGGER.warn("An unsupported comparison type of {} was given", filter.getComparisonType());

                throw new ServiceProblemException(400, "Only the AND comparison type is supported by MDH");
            }

            if (filter.hasWhere()) {
                // we inform the user if a filter is not supported
                filter.getWhere().stream()
                        .filter(listFilterWhere -> QuarantineEntryConstants.STATUS_FIELD.equals(listFilterWhere.getColumnName()) == false &&
                                QuarantineEntryConstants.SOURCE_ID_FIELD.equals(listFilterWhere.getColumnName()) == false &&
                                QuarantineEntryConstants.SOURCE_ENTITY_ID_FIELD.equals(listFilterWhere.getColumnName()) == false &&
                                QuarantineEntryConstants.CREATED_DATE_FIELD.equals(listFilterWhere.getColumnName()) == false &&
                                QuarantineEntryConstants.END_DATE_FIELD.equals(listFilterWhere.getColumnName()) == false &&
                                QuarantineEntryConstants.CAUSE_FIELD.equals(listFilterWhere.getColumnName()) == false &&
                                QuarantineEntryConstants.RESOLUTION_FIELD.equals(listFilterWhere.getColumnName()) == false)
                        .findFirst()
                        .ifPresent(listFilterWhere -> {
                            LOGGER.warn("An unsupported filter of {} was given", listFilterWhere.getColumnName());
                            throw new ServiceProblemException(400, String.format("An unsupported filter of %s was given", listFilterWhere.getColumnName()));
                        });

                // Source ID
                ListFilters.findFilterValue(filter.getWhere(), QuarantineEntryConstants.SOURCE_ID_FIELD, CriteriaType.Equal)
                        .ifPresent(queryFilter::setSourceId);

                // Source Entity ID
                ListFilters.findFilterValue(filter.getWhere(), QuarantineEntryConstants.SOURCE_ENTITY_ID_FIELD, CriteriaType.Equal)
                        .ifPresent(queryFilter::setSourceEntityId);

                // Created Date
                DateFilter createdDateFilter = new DateFilter();

                filter.getWhere().stream()
                        .filter(where -> where.getColumnName().equals(QuarantineEntryConstants.CREATED_DATE_FIELD))
                        .forEachOrdered(Dates.createDateFilter(createdDateFilter));

                queryFilter.setCreatedDate(createdDateFilter);

                // End Date
                DateFilter endDateFilter = new DateFilter();

                filter.getWhere().stream()
                        .filter(where -> where.getColumnName().equals(QuarantineEntryConstants.END_DATE_FIELD))
                        .forEachOrdered(Dates.createDateFilter(endDateFilter));

                queryFilter.setEndDate(endDateFilter);

                // Cause
                List<String> causes = ListFilters.findEnumerableFilterValues(filter.getWhere(), QuarantineEntryConstants.CAUSE_FIELD, VALID_CAUSES);
                if (causes.isEmpty() == false) {
                    queryFilter.setCauses(causes);
                }

                // Resolution
                List<String> resolutions = ListFilters.findEnumerableFilterValues(filter.getWhere(), QuarantineEntryConstants.RESOLUTION_FIELD, VALID_RESOLUTIONS);
                if (resolutions.isEmpty() == false) {
                    queryFilter.setResolutions(resolutions);
                }
            }
        }

        return queryFilter;
    }
}
