package org.jvnet.hudson.maven.plugins.hudson;

import java.io.InputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.util.IOUtil;
import org.custommonkey.xmlunit.XMLAssert;
import org.ddsteps.mock.httpserver.JettyMockServer;
import org.ddsteps.mock.httpserver.JettyMockServer.Callback;
import org.jvnet.hudson.maven.plugins.hudson.AddTwitterMojo;
import org.xml.sax.InputSource;

import com.mtvi.plateng.testing.ReflectionHelper;

public class AddTwitterMojoTest extends AbstractWithServerTestCase {
    private String version;

    public void testMultiProject() throws Exception {
        JettyMockServer mockserver = new JettyMockServer(server);
        mockserver.expect("/hudson/api/xml", createJobListCallback("/unit/multijob.xml"));
        mockserver.expect("/hudson/job/job1/config.xml", createConfigGetCallback("job1"));
        mockserver.expect("/hudson/job/job1/config.xml", createConfigPostCallback("job1"));
        mockserver.expect("/hudson/job/job2/config.xml", createConfigGetCallback("job2"));
        mockserver.expect("/hudson/job/job2/config.xml", createConfigPostCallback("job2"));
        AddTwitterMojo mojo = new AddTwitterMojo();
        assertNotNull(mojo);
        ReflectionHelper.setField(mojo, "hudsonURL", new URL("http://localhost:3434/hudson"));
        ReflectionHelper.setField(mojo, "doAll", true);
        mojo.execute();
        mockserver.verify();
    }

    public void testSingleProject() throws Exception {
        JettyMockServer mockserver = new JettyMockServer(server);
        mockserver.expect("/hudson/api/xml", createJobListCallback("/unit/singlejob.xml"));
        mockserver.expect("/hudson/job/job1/config.xml", createConfigGetCallback("job1"));
        mockserver.expect("/hudson/job/job1/config.xml", createConfigPostCallback("job1"));
        AddTwitterMojo mojo = new AddTwitterMojo();
        assertNotNull(mojo);
        ReflectionHelper.setField(mojo, "hudsonURL", new URL("http://localhost:3434/hudson"));
        ReflectionHelper.setField(mojo, "doAll", true);
        mojo.execute();
        mockserver.verify();
    }

    public void testSingleProjectByName() throws Exception {
        JettyMockServer mockserver = new JettyMockServer(server);
        mockserver.expect("/hudson/job/job1/config.xml", createConfigGetCallback("job1"));
        mockserver.expect("/hudson/job/job1/config.xml", createConfigPostCallback("job1"));
        AddTwitterMojo mojo = new AddTwitterMojo();
        assertNotNull(mojo);
        ReflectionHelper.setField(mojo, "hudsonURL", new URL("http://localhost:3434/hudson"));
        ReflectionHelper.setField(mojo, "jobName", "job1");
        mojo.execute();
        mockserver.verify();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private Callback createConfigGetCallback(final String jobName) {
        return new Callback() {

            public void onExpectedRequest(String target, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
                assertEquals("GET", request.getMethod());
                response.setStatus(200);
                response.setContentType("application/xml");
                InputStream jobStream = getClass().getResourceAsStream(
                        "/unit/before-" + jobName + ".xml");
                IOUtil.copy(jobStream, response.getWriter());

                response.getWriter().flush();
            }

        };
    }

    private Callback createConfigPostCallback(final String jobName) {
        return new Callback() {

            public void onExpectedRequest(String target, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
                assertEquals("POST", request.getMethod());
                response.setStatus(200);
                response.setContentType("application/xml");
                InputStream jobStream = getClass().getResourceAsStream(
                        "/unit/after-" + jobName + ".xml");
                XMLAssert.assertXMLEqual(new InputSource(jobStream), new InputSource(request
                        .getInputStream()));
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
