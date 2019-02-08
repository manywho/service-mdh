package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.universes.UniverseTypeProvider;
import com.google.inject.AbstractModule;
import com.manywho.sdk.services.types.TypeProvider;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TypeProvider.class).to(UniverseTypeProvider.class);
    }
}
