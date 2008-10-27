package org.jvnet.hudson.maven.plugins.hudson;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.mortbay.jetty.Server;

public abstract class AbstractWithServerTestCase extends AbstractMojoTestCase {
    protected Server server;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new Server(3434);
        server.start();
    }

    @Override
    protected void tearDown() throws Exception {
        server.stop();
        super.tearDown();
    }
}
