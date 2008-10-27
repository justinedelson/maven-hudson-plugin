/**
 * 
 */
package org.jvnet.hudson.maven.plugins.hudson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Base class for working with Hudson.
 * 
 * @author edelsonj
 * 
 */
public abstract class AbstractHudsonMojo extends AbstractMojo {

    /**
     * HttpClient instance for methods to use.
     */
    protected final HttpClient httpClient = new HttpClient();

    /**
     * Hudson URL.
     * 
     * @parameter expression="${hudson.url}"
     */
    protected URL hudsonURL;

    /**
     * The Maven Project Object.
     * 
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * Maven settings.
     * 
     * @parameter expression="${settings}"
     * @readonly
     */
    protected Settings settings;

    /**
     * XMLOutputter instance for methods to use.
     */
    private final XMLOutputter xmlOutputter = new XMLOutputter();

    /**
     * Configure a Hudson plugin by adding or replacing its configuration in the
     * configuration document.
     * 
     * @param jobDoc the job Document
     * @param pluginType the type of Plugin (publisher, trigger, etc.)
     * @param element the new plugin element
     */
    protected void configurePlugin(Document jobDoc, PluginType pluginType, Element element) {
        Element plugins = jobDoc.getRootElement().getChild(pluginType.name());
        if (plugins.getChild(element.getName()) != null) {
            plugins.removeChild(element.getName());
        }
        plugins.addContent(element);
    }

    /**
     * Create the base URL for Hudson. Will first look at the ciManagement
     * section of the pom and then override that with the hudson.url property
     * (if available).
     * 
     * @return the Base URL for hudson (without a trailing slash)
     * @throws MojoExecutionException if there's no hudson url
     * @throws MalformedURLException if the hudson url is badly formed
     */
    protected void createHudsonURL() throws MojoExecutionException {
        try {
            if (hudsonURL == null) {
                if ((project != null) && (project.getCiManagement() != null)
                        && ("hudson".equals(project.getCiManagement().getSystem()))) {
                    hudsonURL = new URL(project.getCiManagement().getUrl());
                }
            }

            if (hudsonURL == null) {
                throw new MojoExecutionException("No hudson url defined.");
            }

            String hudsonURLAsString = hudsonURL.toString();
            if (hudsonURLAsString.endsWith("/")) {
                hudsonURLAsString = hudsonURLAsString.substring(0, hudsonURLAsString.length() - 1);
                hudsonURL = new URL(hudsonURLAsString);
            }
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Unable to create URL", e);
        }

        getLog().info("Base Hudson URL is " + hudsonURL);

    }

    /**
     * Create the URL for building a job from Hudson.
     * 
     * @param jobName The name of the job
     * @return the URL to post to to run a build on Hudson
     * @throws MojoExecutionException
     */
    protected URL createJobBuildURL(String jobName) throws MojoExecutionException {
        jobName = jobName.replace(" ", "%20");
        try {
            return new URL(String.format("%s/job/%s/build", hudsonURL, jobName));
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Unable to construct a job build URL", e);
        }
    }

    /**
     * Create the URL for getting a job config in Hudson.
     * 
     * @param jobName The name of the job
     * @return the URL of the job config in Hudson
     * @throws MojoExecutionException if something goes wrong
     */
    protected URL createJobConfigURL(String jobName) throws MojoExecutionException {
        jobName = jobName.replace(" ", "%20");
        try {
            return new URL(String.format("%s/job/%s/config.xml", hudsonURL, jobName));
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Unable to construct a job build URL", e);
        }
    }

    /**
     * Create the URL for getting a job list from Hudson.
     * 
     * @return the URL to get a job list from on Hudson
     * @throws MojoExecutionException if something goes wrong
     */
    protected URL createJobListURL() throws MojoExecutionException {
        try {
            return new URL(String.format("%s/api/xml?xpath=/hudson/job/name&wrapper=jobs",
                    hudsonURL));
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Unable to construct a job list URL", e);
        }
    }

    /**
     * If a child element exists, change it's value. Otherwise, create a new
     * element with the specified name and text.
     * 
     * @param element the parent Element
     * @param name the child element name
     * @param value the child element's new text value
     */
    protected void createOrChange(Element element, String name, int value) {
        createOrChange(element, name, String.valueOf(value));

    }

    /**
     * If a child element exists, change it's value. Otherwise, create a new
     * element with the specified name and text.
     * 
     * @param element the parent Element
     * @param name the child element name
     * @param value the child element's new text value
     */
    protected void createOrChange(Element element, String name, String value) {
        Element child = element.getChild(name);
        if (child == null) {
            child = new Element(name);
            element.addContent(child);
        }
        child.setText(value);
    }

    /**
     * Make an HTTP Post to the URL, erroring out if the returned status doesn't
     * make an expectation.
     * 
     * @param url the URL to POST to.
     * @param expectedStatus expected HTTP status code
     * @throws IOException if something goes wrong
     * @throws MojoExecutionException if the status doesn't match
     */
    protected void doPost(URL url, int expectedStatus) throws IOException, MojoExecutionException {
        doPost(url, null, expectedStatus);
    }

    /**
     * Make an HTTP Post to the URL, erroring out if the returned status doesn't
     * make an expectation.
     * 
     * @param url the URL to POST to.
     * @param requestEntity an entity to include with the POST.
     * @param expectedStatus expected HTTP status code.
     * @throws IOException if something goes wrong
     * @throws MojoExecutionException if the status doesn't match
     */
    protected void doPost(URL url, RequestEntity requestEntity, int expectedStatus)
            throws IOException, MojoExecutionException {
        PostMethod pm = new PostMethod(url.toString());
        if (requestEntity != null) {
            pm.setRequestEntity(requestEntity);
        }
        int status = httpClient.executeMethod(pm);
        if (status != expectedStatus) {
            throw new MojoExecutionException("Incorrect status back from Hudson: " + status
                    + ". Expected: " + expectedStatus);
        }
    }

    /**
     * Search the Maven Settings object for a Server object that matches the URL
     * provided. If the port isn't provided or is the default, then the Server
     * ID is expected to be just the hostname. If the port is provided and is
     * not the default, the Server ID is hostname:port.
     * 
     * @return the corresponding Server object or null
     */
    protected Server findServer() {
        if (settings == null) {
            return null;
        }
        String serverID = hudsonURL.getHost();
        if ((hudsonURL.getPort() != -1) && (hudsonURL.getPort() != hudsonURL.getDefaultPort())) {
            serverID = serverID + ":" + hudsonURL.getPort();
        }
        return settings.getServer(serverID);
    }

    /**
     * Perform some task for each job in the CI server.
     * 
     * @param closure the action to take per job
     * @throws MojoExecutionException if something goes wrong
     */
    protected void forEachJob(JobClosure closure) throws MojoExecutionException {
        List<String> jobList = null;
        try {
            jobList = getJobList();
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to obtain job list", e);
        }

        for (String job : jobList) {
            closure.doWithJob(job);

        }
    }

    protected Document getDocumentFromHudson(URL url) throws MojoExecutionException {
        try {
            GetMethod gm = new GetMethod(url.toString());
            int status = httpClient.executeMethod(gm);
            if (status != HttpStatus.SC_OK) {
                throw new MojoExecutionException("Non-OK status back from Hudson: " + status);
            }

            return new SAXBuilder().build(gm.getResponseBodyAsStream());
        } catch (JDOMException e) {
            throw new MojoExecutionException("Unable to parse " + url.toString(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to parse " + url.toString(), e);
        }
    }

    /**
     * Fetch the job list from Hudson.
     * 
     * @throws MojoExecutionException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    protected List<String> getJobList() throws MojoExecutionException, IOException {
        URL jobListURL = createJobListURL();
        Document doc = getDocumentFromHudson(jobListURL);

        List<Element> nodes = doc.getRootElement().getChildren("name");
        List<String> retval = new ArrayList<String>(nodes.size());
        for (Element node : nodes) {
            retval.add(node.getTextNormalize());
        }

        return retval;
    }

    protected Document loadJobConfig(String name) throws MojoExecutionException {
        URL configURL = createJobConfigURL(name);
        return getDocumentFromHudson(configURL);
    }

    /**
     * Request a rebuild of the job.
     * 
     * @param job the job name
     * @throws MojoExecutionException
     */
    protected void rebuildJob(String job) throws MojoExecutionException {
        URL buildURL = createJobBuildURL(job);
        try {
            doPost(buildURL, HttpStatus.SC_MOVED_TEMPORARILY);
        } catch (HttpException e) {
            throw new MojoExecutionException("Unable to rebuild job " + job, e);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to rebuild job " + job, e);
        }
    }

    /**
     * Save the job's configuration.
     * 
     * @param name the job name
     * @param jobDoc the job config document
     * @throws MojoExecutionException
     */
    protected void saveJobConfig(String name, Document jobDoc) throws MojoExecutionException {
        URL configURL = createJobConfigURL(name);
        String payload = xmlOutputter.outputString(jobDoc);
        try {
            doPost(configURL, new StringRequestEntity(payload, "text/xml", "UTF-8"),
                    HttpStatus.SC_OK);
        } catch (IOException e) {
            throw new MojoExecutionException("Exception posting payload to " + configURL, e);
        }

    }

    /**
     * Configure the HttpClient instance with any necessary authentication
     * parameters.
     * 
     * @param client
     * @param hudsonURL
     */
    protected void setupClient() {
        Server server = findServer();
        if (server != null) {
            httpClient.getParams().setAuthenticationPreemptive(true);
            Credentials defaultcreds = new UsernamePasswordCredentials(server.getUsername(), server
                    .getPassword());
            httpClient.getState().setCredentials(
                    new AuthScope(hudsonURL.getHost(), hudsonURL.getPort(), AuthScope.ANY_REALM),
                    defaultcreds);
        }
    }

    /**
     * Callback interface for the forEachJob method.
     * 
     */
    protected interface JobClosure {

        /**
         * Perform some task for each job.
         * 
         * @param jobName the name of the job
         * @throws MojoExecutionException if something goes wrong
         */
        void doWithJob(String jobName) throws MojoExecutionException;
    }

    /**
     * Enum that defines the update types we can add plugins configurations for.
     * 
     */
    protected enum PluginType {
        builders, publishers, triggers
    }

}
