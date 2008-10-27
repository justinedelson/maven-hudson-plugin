package org.jvnet.hudson.maven.plugins.hudson;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ddsteps.mock.httpserver.JettyMockServer;
import org.ddsteps.mock.httpserver.JettyMockServer.Callback;
import org.jvnet.hudson.maven.plugins.hudson.AddJobMojo;

public class JobExistsMojoTest extends AbstractWithServerTestCase {

    public void testNoJob() throws Exception {
        JettyMockServer mockserver = new JettyMockServer(server);
        mockserver.expect("/hudson/api/xml", new Callback() {

            public void onExpectedRequest(String target, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
                assertEquals("/hudson/job[name='test-project']", request.getParameter("xpath"));
                response.setStatus(404);
                response.getWriter().println("XPath /hudson/job[name='test-project']didn't match");
                response.getWriter().flush();
            }

        });
        File testPom = new File(getBasedir(), "src/test/resources/unit/pom-with-local-ci.xml");
        AddJobMojo mojo = (AddJobMojo) lookupMojo("add-job", testPom);
        assertNotNull(mojo);

        mojo.createHudsonURL();
        assertEquals("http://localhost:3434/hudson", mojo.hudsonURL.toString());

        assertFalse(mojo.jobExists());

        mockserver.verify();
    }

    public void testYesJob() throws Exception {
        JettyMockServer mockserver = new JettyMockServer(server);
        mockserver.expect("/hudson/api/xml", new Callback() {

            public void onExpectedRequest(String target, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
                assertEquals("/hudson/job[name='test-project']", request.getParameter("xpath"));
                response
                        .getWriter()
                        .println(
                                "<job><name>cal</name><url>http://localhost:3333/hudson/job/test-project/</url><color>blue</color></job>");
                response.getWriter().flush();

            }

        });
        File testPom = new File(getBasedir(), "src/test/resources/unit/pom-with-local-ci.xml");
        AddJobMojo mojo = (AddJobMojo) lookupMojo("add-job", testPom);
        assertNotNull(mojo);

        mojo.createHudsonURL();
        assertEquals("http://localhost:3434/hudson", mojo.hudsonURL.toString());

        assertTrue(mojo.jobExists());

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
}
