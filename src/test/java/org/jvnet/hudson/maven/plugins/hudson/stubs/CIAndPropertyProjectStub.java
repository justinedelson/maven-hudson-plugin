package org.jvnet.hudson.maven.plugins.hudson.stubs;

import org.apache.maven.model.CiManagement;

public class CIAndPropertyProjectStub extends BasePropertyProjectStub {
    private CiManagement ciManagement = new CiManagement();

    public CIAndPropertyProjectStub() {
        super();
        setProperty("hudson.url", "http://www.test.com/hudson");
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
