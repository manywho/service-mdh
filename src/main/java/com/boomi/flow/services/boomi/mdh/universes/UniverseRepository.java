package com.boomi.flow.services.boomi.mdh.universes;

import com.boomi.flow.services.boomi.mdh.client.MdhClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UniverseRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(UniverseRepository.class);

    private final MdhClient client;

    @Inject
    public UniverseRepository(MdhClient client) {
        this.client = client;
    }

    public List<Universe> findAll(String atomHostname, String atomUsername, String atomPassword) {
        LOGGER.info("Loading all universes from the Atom at {} with the username {}", atomHostname, atomUsername);

        List<Universe> universes = client.findAllUniverses(atomHostname, atomUsername, atomPassword);

        return Optional.ofNullable(universes)
                .orElse(new ArrayList<>());
    }

    public Universe find(String atomHostname, String atomUsername, String atomPassword, String id) {
        LOGGER.info("Loading the universe {} from the Atom at {} with the username {}", id, atomHostname, atomUsername);

        return client.findUniverse(atomHostname, atomUsername, atomPassword, id);
    }
}
