package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.database.MdhTypeProvider;
import com.google.inject.AbstractModule;
import com.manywho.sdk.services.types.TypeProvider;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TypeProvider.class).to(MdhTypeProvider.class);
    }
}
