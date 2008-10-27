package org.jvnet.hudson.maven.plugins.hudson;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ddsteps.mock.httpserver.JettyMockServer.Callback;
import org.jvnet.hudson.maven.plugins.hudson.AddJobMojo;

import com.mtvi.plateng.testing.http.JettyMockServerWithAuth;

public class ExecuteWithAuthTest extends AbstractWithServerTestCase {
    public void testNoJob() throws Exception {
        JettyMockServerWithAuth mockserver = new JettyMockServerWithAuth(server,
                "src/test/resources/unit/realm.properties");
        mockserver.expect("/hudson/api/xml", new Callback() {

            public void onExpectedRequest(String target, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
                assertEquals("testuser", request.getUserPrincipal().getName());
                assertEquals("/hudson/job[name='test-project']", request.getParameter("xpath"));
                response.setStatus(404);
                response.getWriter().println("XPath /hudson/job[name='test-project']didn't match");
                response.getWriter().flush();
            }

        });
        mockserver.expect("/hudson/createItem", new Callback() {

            public void onExpectedRequest(String target, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
                assertEquals("testuser", request.getUserPrincipal().getName());
                assertEquals("test-project", request.getParameter("name"));
                assertEquals("POST", request.getMethod());
                response.setStatus(200);
                response.getWriter().println("Thanks");
                response.getWriter().flush();
            }

        });

        File testPom = new File(getBasedir(),
                "src/test/resources/unit/pom-with-local-ci-and-auth.xml");
        AddJobMojo mojo = (AddJobMojo) lookupMojo("add-job", testPom);
        assertNotNull(mojo);

        mojo.execute();

        mockserver.verify();
    }

    public void testYesJob() throws Exception {
        JettyMockServerWithAuth mockserver = new JettyMockServerWithAuth(server,
                "src/test/resources/unit/realm.properties");
        mockserver.expect("/hudson/api/xml", new Callback() {

            public void onExpectedRequest(String target, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
                assertEquals("testuser", request.getUserPrincipal().getName());
                assertEquals("/hudson/job[name='test-project']", request.getParameter("xpath"));
                response
                        .getWriter()
                        .println(
                                "<job><name>cal</name><url>http://localhost:3333/hudson/job/test-project/</url><color>blue</color></job>");
                response.getWriter().flush();

            }

        });
        File testPom = new File(getBasedir(),
                "src/test/resources/unit/pom-with-local-ci-and-auth.xml");
        AddJobMojo mojo = (AddJobMojo) lookupMojo("add-job", testPom);
        assertNotNull(mojo);

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
}
