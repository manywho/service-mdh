package com.boomi.flow.services.boomi.mdh;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ApplicationConfiguration implements Configuration {
    @Configuration.Setting(name = "Atom Hostname", contentType = ContentType.String)
    private String atomHostname;

    @Configuration.Setting(name = "Atom Username", contentType = ContentType.String)
    private String atomUsername;

    @Configuration.Setting(name = "Atom Password", contentType = ContentType.Password)
    private String atomPassword;

    public String getAtomHostname() {
        return atomHostname;
    }

    public String getAtomUsername() {
        return atomUsername;
    }

    public String getAtomPassword() {
        return atomPassword;
    }
}
