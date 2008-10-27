package org.jvnet.hudson.maven.plugins.hudson;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.XMLAssert;

import com.mtvi.plateng.testing.ReflectionHelper;

public class CreatePayloadMojoTest extends TestCase {

    private AddJobMojo mojo;

    public void testCreateFullPayload() throws Exception {
        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B -Daggregate=true clean install cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle javadoc:javadoc",
                        "/project/builders/hudson.tasks.Maven/targets", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver",
                payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);
        XMLAssert.assertXpathNotExists(
                "/project/buildWrappers/org.jvnet.hudson.plugins.port__allocator.PortAllocator",
                payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
    }

    public void testCreateNoCoberturaPayload() throws Exception {
        ReflectionHelper.setField(mojo, "includeCobertura", false);

        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B -Daggregate=true clean install pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle javadoc:javadoc",
                        "/project/builders/hudson.tasks.Maven/targets", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver",
                payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathNotExists(
                "/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
    }

    public void testCreateNoEmailPayload() throws Exception {
        ReflectionHelper.setField(mojo, "includeEmail", false);

        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B -Daggregate=true clean install cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle javadoc:javadoc",
                        "/project/builders/hudson.tasks.Maven/targets", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver",
                payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);

        XMLAssert.assertXpathNotExists("/project/publishers/hudson.tasks.Mailer", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.twitter.TwitterPublisher",
                payload);
    }

    public void testCreateNoJavadocPayload() throws Exception {
        ReflectionHelper.setField(mojo, "includeJavadoc", false);

        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B clean install cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle",
                        "/project/builders/hudson.tasks.Maven/targets", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver",
                payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);
        XMLAssert.assertXpathNotExists(
                "/project/buildWrappers/org.jvnet.hudson.plugins.port__allocator.PortAllocator",
                payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
    }

    public void testCreateNoJunitPayload() throws Exception {
        ReflectionHelper.setField(mojo, "includeJunit", false);

        String payload = mojo.createPayload();
        XMLAssert.assertXpathNotExists(
                "/project/publishers/hudson.tasks.junit.JUnitResultArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
    }

    public void testCreateNoTwitterPayload() throws Exception {
        ReflectionHelper.setField(mojo, "includeTwitter", false);

        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B -Daggregate=true clean install cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle javadoc:javadoc",
                        "/project/builders/hudson.tasks.Maven/targets", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver",
                payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
        XMLAssert.assertXpathNotExists(
                "/project/publishers/hudson.plugins.twitter.TwitterPublisher", payload);
    }

    public void testCreateNoViolationsPayload() throws Exception {
        ReflectionHelper.setField(mojo, "includeViolations", false);

        String payload = mojo.createPayload();

        XMLAssert.assertXpathEvaluatesTo(
                "-fae -B -Daggregate=true clean install cobertura:cobertura javadoc:javadoc",
                "/project/builders/hudson.tasks.Maven/targets", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver",
                payload);
        XMLAssert.assertXpathNotExists(
                "/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists(
                "/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
    }

    public void testCreatePayloadWithAdditionalGoals() throws Exception {
        ReflectionHelper.setField(mojo, "additionalGoals", "utils:test");
        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B -Daggregate=true clean install cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle javadoc:javadoc utils:test",
                        "/project/builders/hudson.tasks.Maven/targets", payload);
    }

    public void testCreatePayloadWithMultiplePortAllocation() throws Exception {
        ReflectionHelper.setField(mojo, "allocatePorts", "PORT1,PORT2");

        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "2",
                        "count(/project/buildWrappers/org.jvnet.hudson.plugins.port__allocator.PortAllocator/ports/org.jvnet.hudson.plugins.port__allocator.DefaultPortType)",
                        payload);

        XMLAssert
                .assertXpathEvaluatesTo(
                        "PORT1",
                        "/project/buildWrappers/org.jvnet.hudson.plugins.port__allocator.PortAllocator/ports/org.jvnet.hudson.plugins.port__allocator.DefaultPortType[1]/name/text()",
                        payload);

        XMLAssert
                .assertXpathEvaluatesTo(
                        "PORT2",
                        "/project/buildWrappers/org.jvnet.hudson.plugins.port__allocator.PortAllocator/ports/org.jvnet.hudson.plugins.port__allocator.DefaultPortType[2]/name/text()",
                        payload);

    }

    public void testCreatePayloadWithSinglePortAllocation() throws Exception {
        ReflectionHelper.setField(mojo, "allocatePorts", "PORT1");

        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "1",
                        "count(/project/buildWrappers/org.jvnet.hudson.plugins.port__allocator.PortAllocator/ports/org.jvnet.hudson.plugins.port__allocator.DefaultPortType)",
                        payload);

        XMLAssert
                .assertXpathEvaluatesTo(
                        "PORT1",
                        "/project/buildWrappers/org.jvnet.hudson.plugins.port__allocator.PortAllocator/ports/org.jvnet.hudson.plugins.port__allocator.DefaultPortType/name/text()",
                        payload);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mojo = new AddJobMojo();
        ReflectionHelper.setField(mojo, "performClean", true);
        ReflectionHelper.setField(mojo, "primaryGoal", "install");
        ReflectionHelper.setField(mojo, "subversionURL",
                "scm:svn:https://subversion.1515.mtvi.com/java/foo/bar/");
        ReflectionHelper.setField(mojo, "description", "description");
    }

}
