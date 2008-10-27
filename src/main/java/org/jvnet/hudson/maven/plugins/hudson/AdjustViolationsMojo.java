/**
 * 
 */
package org.jvnet.hudson.maven.plugins.hudson;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 * Mojo that changes the settings of the Violations Hudson plugin.
 * 
 * @author edelsonj
 * @goal adjust-violations
 * 
 */
public class AdjustViolationsMojo extends AbstractHudsonMojo {

    /**
     * The job name.
     * 
     * @parameter expression="${jobName}"
     */
    private String jobName;

    /**
     * Maximum number of violations for Cloudy status.
     * 
     * @parameter expression="${max}"
     * @required
     */
    private int max;

    /**
     * Minimum number of violations for Cloudy status.
     * 
     * @parameter expression="${min}"
     * @required
     */
    private int min;

    /**
     * Violation number at which the build becomes unstable.
     * 
     * @parameter expression="${unstable}"
     * @required
     */
    private int unstable;

    public void execute() throws MojoExecutionException, MojoFailureException {
        setupClient();
        if (jobName != null) {
            changeJob(jobName);
            rebuildJob(jobName);
        } else {
            throw new MojoExecutionException("No job name specified");
        }

    }

    protected void changeJob(String name) throws MojoExecutionException {
        getLog().info("Modifying job: " + name);
        Document jobDoc = loadJobConfig(name);
        try {
            changeViolations(jobDoc);
        } catch (JDOMException e) {
            throw new MojoExecutionException("Unable to change violations.");
        }
        saveJobConfig(name, jobDoc);
    }

    /**
     * Change the configuration of the Violations plugin with the appropriate
     * min, max, and unstable values.
     * 
     * @param jobDoc
     *            the job Document
     * @return true if the violations Element was changed
     * @throws JDOMException
     *             if the
     */
    @SuppressWarnings("unchecked")
    protected boolean changeViolations(Document jobDoc) throws JDOMException {
        List<Element> typeConfigs = XPath
                .selectNodes(
                        jobDoc,
                        "/project/publishers/hudson.plugins.violations.ViolationsPublisher/config/typeConfigs/entry/hudson.plugins.violations.TypeConfig");
        if (typeConfigs.isEmpty()) {
            return false;
        } else {
            for (Element typeConfig : typeConfigs) {
                createOrChange(typeConfig, "min", min);
                createOrChange(typeConfig, "max", max);
                createOrChange(typeConfig, "unstable", unstable);
            }
            return true;
        }

    }

}
