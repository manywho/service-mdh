package com.boomi.flow.services.boomi.mdh.records;

import com.boomi.flow.services.boomi.mdh.ApplicationConfiguration;
import com.boomi.flow.services.boomi.mdh.universes.Universe;
import com.boomi.flow.services.boomi.mdh.universes.UniverseRepository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementIdFinder {
    // this map groups universeId -> [fieldName -> fieldId]
    private Map<String, Map<String, String>> elementByUniverse = new HashMap<>();
    private UniverseRepository universeRepository;

    @Inject
    public ElementIdFinder(UniverseRepository universeRepository) {
        this.universeRepository = universeRepository;

    }

    public String findIdFromNameOfElement(ApplicationConfiguration configuration, String universeId, String fieldName) {
        lazyLoadUniverse(configuration, universeId);

        return elementByUniverse
                .get(universeId)
                .get(fieldName);
    }

    private void lazyLoadUniverse(ApplicationConfiguration configuration, String universeId) {
        // only fetch data if needed
        if( elementByUniverse.get(universeId) == null) {
            // we need to load the universe
            Universe universe = universeRepository.find(configuration.getHubHostname(), configuration.getHubUsername(), configuration.getHubToken(), universeId);
            elementByUniverse.put(universeId, getNameAndId(universe.getLayout().getModel().getElements()));
        }
    }

    private Map<String, String> getNameAndId(List<Universe.Layout.Model.Element> elements) {
        Map<String, String> map = new HashMap<>();
        for (Universe.Layout.Model.Element element: elements) {
            if (element.getElements() != null && element.getElements().isEmpty() == false) {
                map.putAll(getNameAndId(element.getElements()));
            } else {
                map.put(element.getName(), element.getUniqueId());
            }
        }

        return map;
    }

}
