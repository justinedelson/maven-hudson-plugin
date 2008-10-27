/**
 * 
 */
package org.jvnet.hudson.maven.plugins.hudson;

import org.apache.maven.plugin.MojoExecutionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jvnet.hudson.maven.plugins.hudson.AbstractHudsonMojo.JobClosure;


/**
 * Goal which adds Twitter notifications to a job or all jobs.
 * 
 * @goal add-twitter
 * @requiresProject false
 * @description Adds twiter notifications
 * @author edelsonj
 */
public class AddTwitterMojo extends AbstractActionPerJobMojo implements JobClosure {

    /**
     * Add the TwitterPublisher to the job
     * 
     * @param name
     *            the job name
     * @throws MojoExecutionException
     *             if something goes wrong
     * @see org.jvnet.hudson.maven.plugins.hudson.AbstractHudsonMojo.JobClosure#doWithJob(java.lang.String)
     */
    public void doWithJob(String name) throws MojoExecutionException {
        getLog().info("Modifying job: " + name);
        Document jobDoc = loadJobConfig(name);
        configurePlugin(jobDoc, PluginType.publishers, new Element(
                "hudson.plugins.twitter.TwitterPublisher"));
        saveJobConfig(name, jobDoc);
    }

}
