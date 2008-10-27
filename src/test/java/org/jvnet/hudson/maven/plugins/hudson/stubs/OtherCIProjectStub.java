package org.jvnet.hudson.maven.plugins.hudson.stubs;

import org.apache.maven.model.CiManagement;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

public class OtherCIProjectStub extends MavenProjectStub {
    
    private CiManagement ciManagement = new CiManagement();
    
    public OtherCIProjectStub() {
        super();
        ciManagement.setSystem("cruisecontrol");
        ciManagement.setUrl("http://www.test2.com/hudson");
    }

    public CiManagement getCiManagement() {
        return ciManagement;
    }

    public void setCiManagement(CiManagement ciManagement) {
        this.ciManagement = ciManagement;
    }

}
