package com.mtvi.plateng.maven.hudson.stubs;

public class LocalCIProjectStub extends CIProjectStub {

    public LocalCIProjectStub() {
        super();
        getCiManagement().setUrl("http://localhost:3434/hudson");
    }

}
