package org.jvnet.hudson.maven.plugins.hudson;

import java.io.InputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.util.IOUtil;
import org.ddsteps.mock.httpserver.JettyMockServer;
import org.ddsteps.mock.httpserver.JettyMockServer.Callback;

import com.mtvi.plateng.testing.ReflectionHelper;

public class BuildAllTest extends AbstractWithServerTestCase {
    private String version;

    public void testCreateJobBuildURL() throws Exception {
        BuildAllMojo mojo = new BuildAllMojo();
        ReflectionHelper.setField(mojo, "hudsonURL", new URL("http://localhost:3434/hudson"));
        URL u = mojo.createJobBuildURL("job1");
        assertEquals("http://localhost:3434/hudson/job/job1/build", u.toString());
        u = mojo.createJobBuildURL("job 1");
        assertEquals("http://localhost:3434/hudson/job/job%201/build", u.toString());
    }

    public void testMultiProject() throws Exception {
        JettyMockServer mockserver = new JettyMockServer(server);
        mockserver.expect("/hudson/api/xml", createJobListCallback("/unit/multijob.xml"));
        mockserver.expect("/hudson/job/job1/build", createBuildCallback("job1"));
        mockserver.expect("/hudson/job/job2/build", createBuildCallback("job2"));
        BuildAllMojo mojo = (BuildAllMojo) lookupMojo("org.jvnet.hudson.tools",
                "maven-hudson-plugin", version, "build-all", null);
        assertNotNull(mojo);
        ReflectionHelper.setField(mojo, "hudsonURL", new URL("http://localhost:3434/hudson"));
        mojo.execute();
        mockserver.verify();
    }

    public void testSingleProject() throws Exception {
        JettyMockServer mockserver = new JettyMockServer(server);
        mockserver.expect("/hudson/api/xml", createJobListCallback("/unit/singlejob.xml"));
        mockserver.expect("/hudson/job/job1/build", createBuildCallback("job1"));
        BuildAllMojo mojo = (BuildAllMojo) lookupMojo("org.jvnet.hudson.tools",
                "maven-hudson-plugin", version, "build-all", null);
        assertNotNull(mojo);
        ReflectionHelper.setField(mojo, "hudsonURL", new URL("http://localhost:3434/hudson"));
        mojo.execute();
        mockserver.verify();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        version = System.getProperty("project.version");
        if (version == null) {
            version = "2.0-alpha-2-SNAPSHOT";
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private Callback createBuildCallback(final String jobName) {
        return new Callback() {

            public void onExpectedRequest(String target, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
                assertEquals("POST", request.getMethod());
                response.setStatus(302);
                response.setContentType("text/html");
                response.setHeader("Location", "http://localhost:3434/hudson/job/" + jobName + "/");
                response.getWriter().flush();
            }

        };
    }

    private Callback createJobListCallback(final String xmlPath) {
        return new Callback() {

            public void onExpectedRequest(String target, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
                assertEquals("GET", request.getMethod());
                response.setStatus(200);
                response.setContentType("application/xml");
                InputStream jobStream = getClass().getResourceAsStream(xmlPath);
                IOUtil.copy(jobStream, response.getWriter());
                response.getWriter().flush();
            }

        };
    }

}
