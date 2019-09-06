package com.boomi.flow.services.boomi.mdh.database;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.boomi.flow.services.boomi.mdh.universes.UniverseRepository;
import com.manywho.sdk.api.describe.DescribeServiceRequest;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.services.types.TypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MdhTypeProvider implements TypeProvider<ApplicationConfiguration> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MdhTypeProvider.class);

    private final UniverseRepository repository;

    @Inject
    public MdhTypeProvider(UniverseRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean doesTypeExist(ApplicationConfiguration configuration, String name) {
        if (name == null) {
            return false;
        }

        if (name.endsWith(" quarantine")) {
            return true;
        }

        if (name.endsWith(" golden-record")) {
            return true;
        }

        if (name.endsWith(" match")) {
            return true;
        }

        return false;
    }

    private static void loadModelTypes(List<Universe> universes, List<TypeElement> typeElements) {
        universes.stream()
                .filter(universe -> universe.getLayout() != null && universe.getLayout().getModel() != null)
                .forEach(universe ->
                    FieldMapper.collectTypes(universe.getLayout().getModel().getElements(),
                            universe.getName(), TypeNameGenerator.createModelName(universe.getName()), universe.getName(),
                            universe.getId().toString(), typeElements, true));
    }

    @Override
    public List<TypeElement> describeTypes(ApplicationConfiguration configuration, DescribeServiceRequest request) {
        // TODO: Get this bug fixed in the system flows
        if (request.hasConfigurationValues() == false) {
            return new ArrayList<>();
        }

        var universes = repository.findAll(configuration.getAtomHostname(), configuration.getAtomUsername(), configuration.getAtomPassword());

        var types = new ArrayList<TypeElement>();
        loadModelTypes(universes, types);

        return types;
    }
}
