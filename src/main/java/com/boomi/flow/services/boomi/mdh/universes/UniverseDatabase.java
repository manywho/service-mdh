package com.boomi.flow.services.boomi.mdh.universes;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.manywho.sdk.api.draw.content.Command;
import com.manywho.sdk.api.run.elements.type.ListFilter;
import com.manywho.sdk.services.database.ReadOnlyDatabase;

import javax.inject.Inject;
import java.util.List;

public class UniverseDatabase implements ReadOnlyDatabase<ApplicationConfiguration, Universe> {
    private final UniverseRepository repository;

    @Inject
    public UniverseDatabase(UniverseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Universe find(ApplicationConfiguration configuration, Command command, String id) {
        return repository.find(
                configuration.getAtomHostname(),
                configuration.getAtomUsername(),
                configuration.getAtomPassword(),
                id
        );
    }

    @Override
    public List<Universe> findAll(ApplicationConfiguration configuration, Command command, ListFilter filter) {
        return repository.findAll(
                configuration.getAtomHostname(),
                configuration.getAtomUsername(),
                configuration.getAtomPassword()
        );
    }
}
