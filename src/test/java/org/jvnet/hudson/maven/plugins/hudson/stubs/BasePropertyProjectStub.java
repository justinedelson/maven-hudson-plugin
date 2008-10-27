package org.jvnet.hudson.maven.plugins.hudson.stubs;

import java.util.Properties;

import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

public class BasePropertyProjectStub extends MavenProjectStub {
    private Properties properties = new Properties();
    
    public BasePropertyProjectStub() {
        super();
    }

    public Properties getProperties() {
        return properties;
    }
    
    protected void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}
