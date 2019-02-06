package com.boomi.flow.services.boomi.mdh;

import com.google.inject.AbstractModule;
import okhttp3.OkHttpClient;

import javax.inject.Singleton;

public class ApplicationModuleHttpClient extends AbstractModule {
    @Override
    protected void configure() {
        bind(OkHttpClient.class).toProvider(() -> new OkHttpClient.Builder().build()).in(Singleton.class);
    }
}
