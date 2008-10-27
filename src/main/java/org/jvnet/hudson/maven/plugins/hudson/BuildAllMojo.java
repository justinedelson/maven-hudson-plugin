package org.jvnet.hudson.maven.plugins.hudson;

import org.apache.maven.plugin.MojoExecutionException;
import org.jvnet.hudson.maven.plugins.hudson.AbstractHudsonMojo.JobClosure;


/**
 * Goal which hits a Hudson server and builds all of the jobs.
 * 
 * @goal build-all
 * @requiresProject false
 * @description Builds all jobs on a Hudson server.
 */
public class BuildAllMojo extends AbstractHudsonMojo implements JobClosure {

    /**
     * Rebuild a job.
     * 
     * @param jobName
     *            the name of the job to build
     * @throws MojoExecutionException
     *             if something goes wrong
     * @see org.jvnet.hudson.maven.plugins.hudson.AbstractHudsonMojo.JobClosure#doWithJob(java.lang.String)
     */
    public void doWithJob(String jobName) throws MojoExecutionException {
        getLog().info("Rebuilding job named " + jobName);
        rebuildJob(jobName);
    }

    /**
     * Execute the mojo's logic.
     * 
     * @throws MojoExecutionException
     *             if something goes wrong
     */
    public void execute() throws MojoExecutionException {
        setupClient();
        forEachJob(this);
    }

}
