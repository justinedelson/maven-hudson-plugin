package org.jvnet.hudson.maven.plugins.hudson;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which replaces a Hudson job's configuration
 * 
 * @goal replace-job
 * @aggregator
 */
public class ReplaceJobMojo extends AddJobMojo {

    /**
     * Execute the mojo by calling AddJobMojo's execute method and then
     * rebuilding the job.
     * 
     * @throws MojoExecutionException
     *             if something goes wrong.
     * @see org.jvnet.hudson.maven.plugins.hudson.AddJobMojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException {
        super.execute();
        rebuildJob(getJobName());
    }

    /**
     * Create the POST URL for the configuration of an existing job.
     * 
     * @return the URL to post to
     * @throws MojoExecutionException
     *             if the URL is malformed
     * @see org.jvnet.hudson.maven.plugins.hudson.AddJobMojo#createPostURL()
     */
    @Override
    protected URL createPostURL() throws MojoExecutionException {
        try {
            return new URL(String.format("%s/job/%s/config.xml", hudsonURL, getJobName()));
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Unable to creat post URL", e);
        }
    }

    /**
     * Always returns false to ensure that the job is posted.
     * 
     * @return false
     * @see org.jvnet.hudson.maven.plugins.hudson.AddJobMojo#jobExists()
     */
    @Override
    protected boolean jobExists() {
        return false;
    }

}
