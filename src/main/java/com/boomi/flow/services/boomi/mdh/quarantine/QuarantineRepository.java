package com.boomi.flow.services.boomi.mdh.quarantine;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import com.boomi.flow.services.boomi.mdh.common.DateFilter;
import com.boomi.flow.services.boomi.mdh.common.Dates;
import com.boomi.flow.services.boomi.mdh.common.Entities;
import com.boomi.flow.services.boomi.mdh.common.ListFilters;
import com.boomi.flow.services.boomi.mdh.universes.UniverseRepository;
import com.manywho.sdk.api.ComparisonType;
import com.manywho.sdk.api.CriteriaType;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.Property;
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

    public List<MObject> findAll(ApplicationConfiguration configuration, String universe, ListFilter filter) {
        LOGGER.info("Loading quarantine entries for the universe {} from the Atom at {} with the username {}", universe, configuration.getAtomHostname(), configuration.getAtomUsername());

        var queryFilter = new QuarantineQueryRequest.Filter();

        var queryRequest = new QuarantineQueryRequest()
                .setFilter(queryFilter)
                .setIncludeData(true);

        if (filter != null) {
            if (filter.getLimit() > 200) {
                LOGGER.warn("An unsupported limit of {} was given", filter.getLimit());

                throw new RuntimeException("MDH does not support a limit greater than 200");
            }

            if (filter.getComparisonType() != null && filter.getComparisonType().equals(ComparisonType.Or)) {
                LOGGER.warn("An unsupported comparison type of {} was given", filter.getComparisonType());

                throw new RuntimeException("Only the AND comparison type is supported by MDH");
            }

            if (filter.hasWhere()) {
                // Status
                ListFilters.findConstrainedFilter(filter.getWhere(), QuarantineEntryConstants.STATUS_FIELD, VALID_STATUSES)
                        .ifPresent(queryRequest::setType);

                // Source ID
                ListFilters.findFilterValue(filter.getWhere(), QuarantineEntryConstants.SOURCE_ID_FIELD, CriteriaType.Equal)
                        .ifPresent(queryFilter::setSourceId);

                // Source Entity ID
                ListFilters.findFilterValue(filter.getWhere(), QuarantineEntryConstants.SOURCE_ENTITY_ID_FIELD, CriteriaType.Equal)
                        .ifPresent(queryFilter::setSourceEntityId);

                // Created Date
                var createdDateFilter = new DateFilter();

                filter.getWhere().stream()
                        .filter(where -> where.getColumnName().equals(QuarantineEntryConstants.CREATED_DATE_FIELD))
                        .forEachOrdered(Dates.createDateFilter(createdDateFilter));

                queryFilter.setCreatedDate(createdDateFilter);

                // End Date
                var endDateFilter = new DateFilter();

                filter.getWhere().stream()
                        .filter(where -> where.getColumnName().equals(QuarantineEntryConstants.END_DATE_FIELD))
                        .forEachOrdered(Dates.createDateFilter(endDateFilter));

                queryFilter.setEndDate(endDateFilter);

                // Cause
                var causes = ListFilters.findEnumerableFilterValues(filter.getWhere(), QuarantineEntryConstants.CAUSE_FIELD, VALID_CAUSES);
                if (causes.isEmpty() == false) {
                    queryFilter.setCauses(causes);
                }

                // Resolution
                var resolutions = ListFilters.findEnumerableFilterValues(filter.getWhere(), QuarantineEntryConstants.RESOLUTION_FIELD, VALID_RESOLUTIONS);
                if (resolutions.isEmpty() == false) {
                    queryFilter.setResolutions(resolutions);
                }
            }
        }

        var result = mdhClient.queryQuarantineEntries(configuration.getAtomHostname(), configuration.getAtomUsername(), configuration.getAtomPassword(), universe, queryRequest);
        if (result == null || result.getEntries() == null) {
            return new ArrayList<>();
        }

        return result.getEntries().stream()
                .map(entry -> createQuarantineEntryObject(universe, entry))
                .collect(Collectors.toList());
    }

    private static MObject createQuarantineEntryObject(String universe, QuarantineEntry entry) {
        List<Property> properties = new ArrayList<>();

        properties.add(new Property(QuarantineEntryConstants.CAUSE_FIELD, entry.getCause()));
        properties.add(new Property(QuarantineEntryConstants.CREATED_DATE_FIELD, entry.getCreatedDate()));
        properties.add(new Property(QuarantineEntryConstants.END_DATE_FIELD, entry.getEndDate()));
        properties.add(new Property(QuarantineEntryConstants.REASON_FIELD, entry.getReason()));
        properties.add(new Property(QuarantineEntryConstants.RESOLUTION_FIELD, entry.getResolution()));
        properties.add(new Property(QuarantineEntryConstants.TRANSACTION_ID_FIELD, entry.getTransactionId()));
        properties.add(new Property(QuarantineEntryConstants.SOURCE_ENTITY_ID_FIELD, entry.getSourceEntityId()));

        // Create the object data for the entity
        properties.add(new Property(QuarantineEntryConstants.ENTITY_FIELD, Entities.createEntityMObject(entry.getSourceEntityId(), "Model", entry.getEntity())));

        return new MObject(universe, entry.getTransactionId(), properties);
    }
}
