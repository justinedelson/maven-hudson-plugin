package org.jvnet.hudson.maven.plugins.hudson.stubs;


public class ExtraSlashPropertyProjectStub extends BasePropertyProjectStub {
    
    public ExtraSlashPropertyProjectStub() {
        super();
        setProperty("hudson.url", "http://www.test.com/hudson/");
    }

}
