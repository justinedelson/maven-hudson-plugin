package org.jvnet.hudson.maven.plugins.hudson;

import java.net.URL;

import junit.framework.TestCase;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.jvnet.hudson.maven.plugins.hudson.AddJobMojo;

import com.mtvi.plateng.testing.ReflectionHelper;

public class FindServerTest extends TestCase {

    public void testFindServerHostAndPort() throws Exception {
        Settings settings = new Settings();
        Server server = new Server();
        server.setId("testserver:1234");
        server.setUsername("testuser");
        server.setPassword("testpassword");
        settings.addServer(server);

        AddJobMojo mojo = createMojo(settings);

        URL testURL = new URL("http://testserver:1234/hudson");
        mojo.hudsonURL = testURL;
        Server fromMojoServer = mojo.findServer();
        assertEquals(server, fromMojoServer);
    }

    public void testFindServerJustHost() throws Exception {
        Settings settings = new Settings();
        Server server = new Server();
        server.setId("testserver");
        server.setUsername("testuser");
        server.setPassword("testpassword");
        settings.addServer(server);

        AddJobMojo mojo = createMojo(settings);

        URL testURL = new URL("http://testserver/hudson");
        mojo.hudsonURL = testURL;
        Server fromMojoServer = mojo.findServer();
        assertNotNull(fromMojoServer);
        assertEquals(server, fromMojoServer);
    }

    public void testFindServerJustHost2() throws Exception {
        Settings settings = new Settings();
        Server server = new Server();
        server.setId("testserver");
        server.setUsername("testuser");
        server.setPassword("testpassword");
        settings.addServer(server);

        AddJobMojo mojo = createMojo(settings);

        URL testURL = new URL("http://testserver:80/hudson");
        mojo.hudsonURL = testURL;
        Server fromMojoServer = mojo.findServer();
        assertEquals(server, fromMojoServer);
    }

    public void testFindServerNull() throws Exception {
        Settings settings = new Settings();
        AddJobMojo mojo = createMojo(settings);

        URL testURL = new URL("http://testserver/hudson");
        mojo.hudsonURL = testURL;
        Server server = mojo.findServer();
        assertNull(server);
    }

    protected AddJobMojo createMojo(Settings settings) throws Exception {
        AddJobMojo mojo = new AddJobMojo();
        ReflectionHelper.setField(mojo, "settings", settings);
        return mojo;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

}
