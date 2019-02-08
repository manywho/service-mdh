package com.boomi.flow.services.boomi.mdh.database;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.quarantine.QuarantineRepository;
import com.boomi.flow.services.boomi.mdh.records.GoldenRecordRepository;
import com.manywho.sdk.api.draw.content.Command;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataType;
import com.manywho.sdk.services.database.RawDatabase;

import javax.inject.Inject;
import java.util.List;

public class MdhRawDatabase implements RawDatabase<ApplicationConfiguration> {
    private final QuarantineRepository quarantineRepository;
    private final GoldenRecordRepository goldenRecordRepository;

    @Inject
    public MdhRawDatabase(QuarantineRepository quarantineRepository, GoldenRecordRepository goldenRecordRepository) {
        this.quarantineRepository = quarantineRepository;
        this.goldenRecordRepository = goldenRecordRepository;
    }

    @Override
    public MObject find(ApplicationConfiguration configuration, ObjectDataType objectDataType, Command command, String id) {
        return null;
    }

    @Override
    public List<MObject> findAll(ApplicationConfiguration configuration, ObjectDataType objectDataType, Command command, ListFilter filter) {
        var typeName = objectDataType.getDeveloperName();

        if (typeName.startsWith("quarantine-")) {
            var universe = typeName.replace("quarantine-", "");

            return quarantineRepository.findAll(configuration, universe, filter);
        }

        if (typeName.startsWith("golden-record-")) {
            var universe = typeName.replace("golden-record-", "");

            return goldenRecordRepository.findAll(configuration, universe, filter);
        }

        // TODO
        return null;
    }

    @Override
    public MObject create(ApplicationConfiguration configuration, ObjectDataType objectDataType, MObject object) {
        return create(configuration, objectDataType, List.of(object)).stream()
                .findFirst()
                .orElse(null); // TODO
    }

    @Override
    public List<MObject> create(ApplicationConfiguration configuration, ObjectDataType objectDataType, List<MObject> objects) {
        var typeName = objectDataType.getDeveloperName();

        if (typeName.startsWith("golden-record-")) {
            var universe = typeName.replace("golden-record-", "");

            return goldenRecordRepository.create(configuration, universe, objects);
        }

        // TODO
        return null;
    }

    @Override
    public void delete(ApplicationConfiguration configuration, ObjectDataType objectDataType, MObject object) {

    }

    @Override
    public void delete(ApplicationConfiguration configuration, ObjectDataType objectDataType, List<MObject> objects) {

    }

    @Override
    public MObject update(ApplicationConfiguration configuration, ObjectDataType objectDataType, MObject object) {
        return null;
    }

    @Override
    public List<MObject> update(ApplicationConfiguration configuration, ObjectDataType objectDataType, List<MObject> objects) {
        return null;
    }
}
