package com.boomi.flow.services.boomi.mdh;

import com.manywho.sdk.services.servers.EmbeddedServer;
import com.manywho.sdk.services.servers.undertow.UndertowServer;

public class Application {
    public static void main(String[] args) throws Exception {
        EmbeddedServer server = new UndertowServer();
        server.addModule(new ApplicationModule());
        server.addModule(new ApplicationModuleHttpClient());
        server.setApplication(Application.class);
        server.start();
    }
}
