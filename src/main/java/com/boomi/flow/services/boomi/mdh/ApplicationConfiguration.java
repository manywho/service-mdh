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

    public ApplicationConfiguration setAtomHostname(String atomHostname) {
        this.atomHostname = atomHostname;
        return this;
    }

    public String getAtomUsername() {
        return atomUsername;
    }

    public ApplicationConfiguration setAtomUsername(String atomUsername) {
        this.atomUsername = atomUsername;
        return this;
    }

    public String getAtomPassword() {
        return atomPassword;
    }

    public ApplicationConfiguration setAtomPassword(String atomPassword) {
        this.atomPassword = atomPassword;
        return this;
    }
}
