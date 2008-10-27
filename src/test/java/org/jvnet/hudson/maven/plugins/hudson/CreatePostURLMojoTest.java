package org.jvnet.hudson.maven.plugins.hudson;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.jvnet.hudson.maven.plugins.hudson.AddJobMojo;
import org.jvnet.hudson.maven.plugins.hudson.ReplaceJobMojo;

public class CreatePostURLMojoTest extends AbstractMojoTestCase {

    public void testCreatePostURLFromCI() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/unit/pom-with-only-ci.xml");
        AddJobMojo mojo = (AddJobMojo) lookupMojo("add-job", testPom);
        assertNotNull(mojo);

        mojo.createHudsonURL();
        assertEquals("http://www.test2.com/hudson", mojo.hudsonURL.toString());

        assertEquals("http://www.test2.com/hudson/createItem?name=test-project", mojo
                .createPostURL().toString());
    }

    public void testCreatePostURLFromCIAndProperty() throws Exception {
        File testPom = new File(getBasedir(),
                "src/test/resources/unit/pom-with-ci-and-property.xml");
        AddJobMojo mojo = (AddJobMojo) lookupMojo("add-job", testPom);
        assertNotNull(mojo);

        mojo.createHudsonURL();
        assertEquals("http://www.test.com/hudson", mojo.hudsonURL.toString());

        assertEquals("http://www.test.com/hudson/createItem?name=test-project", mojo
                .createPostURL().toString());
    }

    public void testCreatePostURLFromCIForReplace() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/unit/pom-with-only-ci.xml");
        ReplaceJobMojo mojo = (ReplaceJobMojo) lookupMojo("replace-job", testPom);
        assertNotNull(mojo);

        mojo.createHudsonURL();

        assertEquals("http://www.test2.com/hudson/job/test-project/config.xml", mojo
                .createPostURL().toString());
    }

    public void testCreatePostURLFromOtherCI() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/unit/pom-with-other-ci.xml");
        AddJobMojo mojo = (AddJobMojo) lookupMojo("add-job", testPom);
        assertNotNull(mojo);
        boolean exceptionThrown = false;
        try {
            mojo.createHudsonURL();
        } catch (MojoExecutionException e) {
            assertEquals("No hudson url defined.", e.getMessage());
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    public void testCreatePostURLFromProperty() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/unit/pom-with-property.xml");
        AddJobMojo mojo = (AddJobMojo) lookupMojo("add-job", testPom);
        assertNotNull(mojo);

        mojo.createHudsonURL();
        assertEquals("http://www.test.com/hudson", mojo.hudsonURL.toString());

        assertEquals("http://www.test.com/hudson/createItem?name=test-project", mojo
                .createPostURL().toString());
    }

    public void testCreatePostURLFromPropertyWithExtraSlash() throws Exception {
        File testPom = new File(getBasedir(),
                "src/test/resources/unit/pom-with-property-extra-slash.xml");
        AddJobMojo mojo = (AddJobMojo) lookupMojo("add-job", testPom);
        assertNotNull(mojo);

        mojo.createHudsonURL();
        assertEquals("http://www.test.com/hudson", mojo.hudsonURL.toString());

        assertEquals("http://www.test.com/hudson/createItem?name=test-project", mojo
                .createPostURL().toString());
    }

    public void testCreatePostURLWithNothing() throws Exception {
        File testPom = new File(getBasedir(), "src/test/resources/unit/pom-with-nothing.xml");
        AddJobMojo mojo = (AddJobMojo) lookupMojo("add-job", testPom);
        assertNotNull(mojo);
        boolean exceptionThrown = false;
        try {
            mojo.createHudsonURL();
        } catch (MojoExecutionException e) {
            assertEquals("No hudson url defined.", e.getMessage());
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

}
