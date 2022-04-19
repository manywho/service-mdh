package com.boomi.flow.services.boomi.mdh;

import com.google.inject.AbstractModule;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

public class ApplicationModuleHttpClient extends AbstractModule {
    @Override
    protected void configure() {
        bind(OkHttpClient.class).toProvider(() -> new OkHttpClient.Builder()
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .connectTimeout(120, TimeUnit.SECONDS)
        .build()).in(Singleton.class);
    }
}
