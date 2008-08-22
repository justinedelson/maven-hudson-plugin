package com.mtvi.plateng.maven.hudson.stubs;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

public class AuthSettingsStub extends Settings {

    public AuthSettingsStub() {
        Server server = new Server();
        server.setId("localhost:3434");
        server.setUsername("testuser");
        server.setPassword("testpassword");
        addServer(server);
    }

}
