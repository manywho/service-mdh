package com.boomi.flow.services.boomi.mdh.database;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.match.MatchEntityRepository;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordRepository;
import com.manywho.sdk.api.draw.content.Command;
import com.manywho.sdk.api.run.ServiceProblemException;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.RawDatabase;
import javax.inject.Inject;
import java.util.List;

public class MdhRawDatabase implements RawDatabase<ApplicationConfiguration> {
    private final QuarantineRepository quarantineRepository;
    private final GoldenRecordRepository goldenRecordRepository;
    private final MatchEntityRepository matchEntityRespository;

    @Inject
    public MdhRawDatabase(QuarantineRepository quarantineRepository, GoldenRecordRepository goldenRecordRepository,
                          MatchEntityRepository matchEntityRepository) {
        this.quarantineRepository = quarantineRepository;
        this.goldenRecordRepository = goldenRecordRepository;
        this.matchEntityRespository = matchEntityRepository;
    }

    @Override
    public MObject find(ApplicationConfiguration configuration, ObjectDataType objectDataType, Command command, String id) {
        return null;
    }

    @Override
    public List<MObject> findAll(ApplicationConfiguration configuration, ObjectDataType objectDataType, Command command, ListFilter filter) {
        var typeName = objectDataType.getDeveloperName();

        if (typeName.endsWith("quarantine")) {
            var universe = removeEndingSubstring(typeName, " quarantine");

            return quarantineRepository.findAll(configuration, universe, filter);
        }

        if (typeName.endsWith("golden-record")) {
            var universe = removeEndingSubstring(typeName, " golden-record");

            return goldenRecordRepository.findAll(configuration, universe, filter);
        }

        // TODO
        return null;
    }

    private String removeEndingSubstring(String original, String ending) {
        return original.substring(0, original.length() - ending.length());
    }

    @Override
    public MObject create(ApplicationConfiguration configuration, ObjectDataType objectDataType, MObject object) {
        return update(configuration, objectDataType, object);
    }

    @Override
    public List<MObject> create(ApplicationConfiguration configuration, ObjectDataType objectDataType, List<MObject> objects) {
        return update(configuration, objectDataType, objects);
    }

    @Override
    public void delete(ApplicationConfiguration configuration, ObjectDataType objectDataType, MObject object) {
        delete(configuration, objectDataType, List.of(object));
    }

    @Override
    public void delete(ApplicationConfiguration configuration, ObjectDataType objectDataType, List<MObject> objects) {
        var typeName = objectDataType.getDeveloperName();

        if (typeName.endsWith("golden-record")) {
            var universe = typeName.replace(" golden-record", "");

            goldenRecordRepository.delete(configuration, universe, objects);
            return;
        }

        throw new ServiceProblemException(400, "The type " + typeName + " does not support deleting");
    }

    @Override
    public MObject update(ApplicationConfiguration configuration, ObjectDataType objectDataType, MObject object) {
        return update(configuration, objectDataType, List.of(object))
                .stream()
                .findFirst()
                .orElseThrow(() -> new ServiceProblemException(400, "No object was returned in the update request"));
    }

    @Override
    public List<MObject> update(ApplicationConfiguration configuration, ObjectDataType objectDataType, List<MObject> objects) {
        var typeName = objectDataType.getDeveloperName();

        if (typeName.endsWith("golden-record")) {
            var universe = removeEndingSubstring(typeName," golden-record");

            return goldenRecordRepository.update(configuration, universe, objects);
        }

        if (typeName.endsWith("match")) {
            var universe = removeEndingSubstring(typeName," match");

            return matchEntityRespository.matchEntity(configuration, universe, objects);
        }

        throw new ServiceProblemException(400, "The type " + typeName + " does not support saving");
    }
}
