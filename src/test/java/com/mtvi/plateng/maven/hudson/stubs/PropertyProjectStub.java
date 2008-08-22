package com.mtvi.plateng.maven.hudson.stubs;


public class PropertyProjectStub extends BasePropertyProjectStub {
    
    public PropertyProjectStub() {
        super();
        setProperty("hudson.url", "http://www.test.com/hudson");
    }

}
