package org.jvnet.hudson.maven.plugins.hudson;

import org.apache.maven.plugin.MojoExecutionException;
import org.jvnet.hudson.maven.plugins.hudson.AbstractHudsonMojo.JobClosure;


/**
 * Abstract base class for mojos that need to do something for all jobs or just
 * one job.
 * 
 * @author edelsonj
 * 
 */
public abstract class AbstractActionPerJobMojo extends AbstractHudsonMojo implements JobClosure {

    /**
     * Do the thing for every job?
     * 
     * @parameter expression="${doAll}"
     */
    private boolean doAll;

    /**
     * The job name.
     * 
     * @parameter expression="${jobName}"
     */
    private String jobName;

    /**
     * Execute the mojo.
     * 
     * @throws MojoExecutionException
     *             if something goes wrong
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException {
        createHudsonURL();
        setupClient();
        if (doAll) {
            forEachJob(this);
        } else if (jobName != null) {
            doWithJob(jobName);
        } else {
            throw new MojoExecutionException("No job name specified");
        }

    }

}
