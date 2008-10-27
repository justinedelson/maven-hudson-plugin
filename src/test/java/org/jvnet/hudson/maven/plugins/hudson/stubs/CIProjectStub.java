package org.jvnet.hudson.maven.plugins.hudson.stubs;

import org.apache.maven.model.CiManagement;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

public class CIProjectStub extends MavenProjectStub {
    
    private CiManagement ciManagement = new CiManagement();
    
    public CIProjectStub() {
        super();
        ciManagement.setSystem("hudson");
        ciManagement.setUrl("http://www.test2.com/hudson");
    }

    public CiManagement getCiManagement() {
        return ciManagement;
    }

    public void setCiManagement(CiManagement ciManagement) {
        this.ciManagement = ciManagement;
    }

}
