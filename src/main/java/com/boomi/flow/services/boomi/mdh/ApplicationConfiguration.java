package com.boomi.flow.services.boomi.mdh;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ApplicationConfiguration implements Configuration {
    @Configuration.Setting(name = "Hub Hostname", contentType = ContentType.String)
    private String hubHostname;

    @Configuration.Setting(name = "Hub Username", contentType = ContentType.String)
    private String hubUsername;

    @Configuration.Setting(name = "Hub Token", contentType = ContentType.Password)
    private String hubToken;

    public String getHubHostname() {
        return hubHostname;
    }

    public ApplicationConfiguration setHubHostname(String hubHostname) {
        this.hubHostname = hubHostname;
        return this;
    }

    public String getHubUsername() {
        return hubUsername;
    }

    public ApplicationConfiguration setHubUsername(String hubUsername) {
        this.hubUsername = hubUsername;
        return this;
    }

    public String getHubToken() {
        return hubToken;
    }

    public ApplicationConfiguration setHubToken(String hubToken) {
        this.hubToken = hubToken;
        return this;
    }
}
