package org.jvnet.hudson.maven.plugins.hudson;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.custommonkey.xmlunit.XMLAssert;

public class CreatePayloadMojoTest extends TestCase {

    private AddJobMojo mojo;

    public void testCreateFullPayload() throws Exception {
        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B -Daggregate=true clean install cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle javadoc:javadoc",
                        "/project/builders/hudson.tasks.Maven/targets", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);
        XMLAssert.assertXpathNotExists("/project/buildWrappers/org.jvnet.hudson.plugins.port__allocator.PortAllocator",
                payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);

        XMLAssert.assertXpathEvaluatesTo("hudson.scm.SubversionSCM",
                "/project/scm/@class", payload);

        XMLAssert.assertXpathEvaluatesTo("https://subversion.1515.mtvi.com/java/foo/bar/",
                "/project/scm/locations/hudson.scm.SubversionSCM_-ModuleLocation/remote", payload);
    }

    public void testCreateWithGit() throws Exception {
        PrivateAccessor.setField(mojo, "scmUrl", "scm:git:git@github.com/justinedelson/maven-misc-plugin.git");


        String payload = mojo.createPayload();

        XMLAssert.assertXpathEvaluatesTo("hudson.plugins.git.GitSCM",
                "/project/scm/@class", payload);

        XMLAssert.assertXpathEvaluatesTo("git@github.com/justinedelson/maven-misc-plugin.git",
                "/project/scm/remoteRepositories/org.spearce.jgit.transport.RemoteConfig/string[9]", payload);
    }

    public void testCreateNoCoberturaPayload() throws Exception {
        PrivateAccessor.setField(mojo, "includeCobertura", false);

        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B -Daggregate=true clean install pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle javadoc:javadoc",
                        "/project/builders/hudson.tasks.Maven/targets", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathNotExists("/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
    }

    public void testCreateNoEmailPayload() throws Exception {
        PrivateAccessor.setField(mojo, "includeEmail", false);

        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B -Daggregate=true clean install cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle javadoc:javadoc",
                        "/project/builders/hudson.tasks.Maven/targets", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);

        XMLAssert.assertXpathNotExists("/project/publishers/hudson.tasks.Mailer", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.twitter.TwitterPublisher", payload);
    }

    public void testCreateNoJavadocPayload() throws Exception {
        PrivateAccessor.setField(mojo, "includeJavadoc", false);

        String payload = mojo.createPayload();

        XMLAssert.assertXpathEvaluatesTo(
                "-fae -B clean install cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle",
                "/project/builders/hudson.tasks.Maven/targets", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);
        XMLAssert.assertXpathNotExists("/project/buildWrappers/org.jvnet.hudson.plugins.port__allocator.PortAllocator",
                payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
    }

    public void testCreateNoJunitPayload() throws Exception {
        PrivateAccessor.setField(mojo, "includeJunit", false);

        String payload = mojo.createPayload();
        XMLAssert.assertXpathNotExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
    }

    public void testCreateNoTwitterPayload() throws Exception {
        PrivateAccessor.setField(mojo, "includeTwitter", false);

        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B -Daggregate=true clean install cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle javadoc:javadoc",
                        "/project/builders/hudson.tasks.Maven/targets", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
        XMLAssert.assertXpathNotExists("/project/publishers/hudson.plugins.twitter.TwitterPublisher", payload);
    }

    public void testCreateNoViolationsPayload() throws Exception {
        PrivateAccessor.setField(mojo, "includeViolations", false);

        String payload = mojo.createPayload();

        XMLAssert.assertXpathEvaluatesTo("-fae -B -Daggregate=true clean install cobertura:cobertura javadoc:javadoc",
                "/project/builders/hudson.tasks.Maven/targets", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.JavadocArchiver", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.junit.JUnitResultArchiver", payload);
        XMLAssert.assertXpathNotExists("/project/publishers/hudson.plugins.violations.ViolationsPublisher", payload);
        XMLAssert.assertXpathExists("/project/publishers/hudson.plugins.cobertura.CoberturaPublisher", payload);

        XMLAssert.assertXpathExists("/project/publishers/hudson.tasks.Mailer", payload);
    }

    public void testCreatePayloadWithAdditionalGoals() throws Exception {
        PrivateAccessor.setField(mojo, "additionalGoals", "utils:test");
        String payload = mojo.createPayload();

        XMLAssert
                .assertXpathEvaluatesTo(
                        "-fae -B -Daggregate=true clean install cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle javadoc:javadoc utils:test",
                        "/project/builders/hudson.tasks.Maven/targets", payload);
    }

    public void testCreatePayloadWithMultiplePortAllocation() throws Exception {
        PrivateAccessor.setField(mojo, "allocatePorts", "PORT1,PORT2");

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
        PrivateAccessor.setField(mojo, "allocatePorts", "PORT1");

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
    public void setUp() throws Exception {
        super.setUp();
        mojo = new AddJobMojo();
        PrivateAccessor.setField(mojo, "performClean", true);
        PrivateAccessor.setField(mojo, "primaryGoal", "install");
        PrivateAccessor.setField(mojo, "scmUrl", "scm:svn:https://subversion.1515.mtvi.com/java/foo/bar/");
        PrivateAccessor.setField(mojo, "description", "description");
    }

}
