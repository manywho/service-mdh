package com.boomi.flow.services.boomi.mdh;

import com.boomi.flow.services.boomi.mdh.guice.XmlMapperProvider;
import com.boomi.flow.services.boomi.mdh.universes.UniverseTypeProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.AbstractModule;
import com.manywho.sdk.services.types.TypeProvider;
import okhttp3.OkHttpClient;

import javax.inject.Singleton;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TypeProvider.class).to(UniverseTypeProvider.class);
        bind(OkHttpClient.class).toProvider(() -> new OkHttpClient.Builder().build()).in(Singleton.class);
        bind(XmlMapper.class).toProvider(XmlMapperProvider.class).in(Singleton.class);
    }
}
