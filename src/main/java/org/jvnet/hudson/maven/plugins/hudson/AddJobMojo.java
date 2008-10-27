package org.jvnet.hudson.maven.plugins.hudson;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Goal which creates a Hudson job.
 * 
 * @goal add-job
 * @aggregator
 */
public class AddJobMojo extends AbstractHudsonMojo {

    /**
     * Any goals to be added when a plugin is enabled.
     */
    private static final Map<String, List<String>> GOALS = new HashMap<String, List<String>>();

    /**
     * List of plugins that can modify the goal list.
     */
    private static final List<String> PLUGINS = Arrays.asList("cobertura", "violations", "javadoc");

    /**
     * Any prefixes to the goal list to be added when a plugin is enabled.
     */
    private static final Map<String, List<String>> PREFIXES = new HashMap<String, List<String>>();

    /**
     * Additional goals to be run after any reporting goals are executed.
     * 
     * @parameter expression="${additionalGoals}"
     */
    private String additionalGoals;

    /**
     * Comma-delimited list of environment variables to have set using the Port
     * Allocator plugin. See
     * http://hudson.gotdns.com/wiki/display/HUDSON/Port+Allocator+Plugin
     * 
     * @parameter expression="${allocatePorts}"
     */
    private String allocatePorts;

    /**
     * Project description.
     * 
     * @parameter expression="${project.description}"
     */
    private String description;

    /**
     * Should the produced project XML include Cobertura Publisher support.
     * 
     * @parameter expression="${includeCobertura}" default-value="true"
     * @required
     */
    private boolean includeCobertura = true;

    /**
     * Should the produced project XML include email support.
     * 
     * @parameter expression="${includeEmail}" default-value="true"
     * @required
     */
    private boolean includeEmail = true;

    /**
     * Should the produced project XML include Javadoc Publisher support.
     * 
     * @parameter expression="${includeJavadoc}" default-value="true"
     * @required
     */
    private boolean includeJavadoc = true;

    /**
     * Should the produced project XML include JUnit Publisher support.
     * 
     * @parameter expression="${includeJunit}" default-value="true"
     * @required
     */
    private boolean includeJunit = true;

    /**
     * Should the produced project XML include twitter support.
     * 
     * @parameter expression="${includeTwitter}" default-value="true"
     * @required
     */
    private boolean includeTwitter = true;

    /**
     * Should the produced project XML include Violations Publisher support.
     * 
     * @parameter expression="${includeViolations}" default-value="true"
     * @required
     */
    private boolean includeViolations = true;

    /**
     * Name of the job to be created.
     * 
     * @parameter expression="${jobName}"
     */
    private String jobName;
    /**
     * Should a clean be performed before the build.
     * 
     * @parameter expression="${peformClean}" default-value="true"
     */
    private boolean performClean;

    /**
     * Primary "build" goal to be executed. Usually either intstall or deploy.
     * 
     * @parameter default-value="install"
     * @required
     */
    private String primaryGoal;

    /**
     * Should the -fae command-line option be passed to Maven, specifying that
     * the build should only report a failure at the end.
     * 
     * @parameter expression="${failAtEnd}" default-value="true"
     */
    private boolean shouldFailAtEnd = true;

    /**
     * Should the -B command-line option be passed to Maven, specifying that the
     * build is running in batch (i.e. non-interactive) mode.
     * 
     * @parameter expression="${useBatchMode}" default-value="true"
     */
    private boolean shouldUseBatchMode = true;

    /**
     * If true, will start a VNC session before each job execution. See
     * http://hudson.gotdns.com/wiki/display/HUDSON/Xvnc+Plugin.
     * 
     * @parameter expression="${startVNC}"
     */
    private boolean startVNC;

    /**
     * Subversion URL.
     * 
     * @parameter expression="${project.scm.developerConnection}"
     * @required
     */
    private String subversionURL;

    static {
        PREFIXES.put("javadoc", Arrays.asList("-Daggregate=true"));

        GOALS.put("cobertura", Arrays.asList("cobertura:cobertura"));
        GOALS.put("javadoc", Arrays.asList("javadoc:javadoc"));
        GOALS.put("violations", Arrays.asList("pmd:pmd", "pmd:cpd", "findbugs:findbugs",
                "checkstyle:checkstyle"));
    }

    /**
     * Add the job to Hudson, if necessary.
     * 
     * @throws MojoExecutionException if something goes wrong
     */
    public void execute() throws MojoExecutionException {
        if (project == null) {
            throw new MojoExecutionException("This mojo must be run in the context of a project.");
        }
        createHudsonURL();

        setupClient();

        if (!jobExists()) {
            URL postURL = createPostURL();
            getLog().info("posting job to " + postURL);

            String payload = createPayload();

            postPayload(postURL, payload);
        }
    }

    /**
     * Create a paylod XML for submission to Hudson.
     * 
     * @return the XML to post to Hudson
     * @throws MojoExecutionException if something goes wrong
     */
    protected String createPayload() throws MojoExecutionException {
        try {
            Velocity.init();
            VelocityContext ctx = new VelocityContext();
            ctx.put("goals", createFullGoals());
            ctx.put("subversionURL", subversionURL.replace("scm:svn:", ""));
            addIncludes(ctx);
            if (allocatePorts != null) {
                ctx.put("ports", allocatePorts.split(","));
            }
            ctx.put("startVNC", Boolean.valueOf(startVNC));
            if (description != null) {
                ctx.put("description", description);
            } else {
                ctx.put("description", "");
            }
            StringWriter sw = new StringWriter();
            Velocity.evaluate(ctx, sw, "project-template.vm", getClass().getResourceAsStream(
                    "/project-template.vm"));
            return sw.toString();
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to evaluate template", e);
        }
    }

    /**
     * Create the URL for posting to Hudson.
     * 
     * @return the URL to post to on Hudson
     * @throws MojoExecutionException
     */
    protected URL createPostURL() throws MojoExecutionException {
        try {
            return new URL(String.format("%s/createItem?name=%s", hudsonURL, getJobName()));
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Unable to creat post URL", e);
        }
    }

    /**
     * Determine the job name. Will use jobName if available.
     * 
     * @return the job name
     */
    protected String getJobName() {
        if (jobName != null) {
            return jobName;
        } else {
            return project.getArtifactId();
        }
    }

    /**
     * Method to check if a job already exists.
     * 
     * @param hudsonURL The Base Hudson URL
     * 
     * @return true if the job already exists in Hudson
     * @throws MojoExecutionException
     */
    protected boolean jobExists() throws MojoExecutionException {
        String jobURL = String.format("%s/api/xml?xpath=/hudson/job[name='%s']", hudsonURL,
                getJobName());
        GetMethod gm = new GetMethod(jobURL);
        try {
            int status = httpClient.executeMethod(gm);
            return status != HttpStatus.SC_NOT_FOUND;
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to check if job currently exists", e);
        }
    }

    /**
     * Post the XML payload to Hudson.
     * 
     * @param postURL The URL to post to
     * @param payload The XML payload
     * @throws MojoExecutionException
     */
    protected void postPayload(URL postURL, String payload) throws MojoExecutionException {
        try {
            doPost(postURL, new StringRequestEntity(payload, "text/xml", "UTF-8"), HttpStatus.SC_OK);
        } catch (IOException e) {
            throw new MojoExecutionException("Exception posting payload to " + postURL, e);
        }
    }

    /**
     * Iterate through the fields for this class and see if they start with
     * include. If they do, add each one to the context.
     * 
     * @param ctx the VelocityContext to add includes to.
     * @throws IllegalAccessException if something goes wrong in reflection
     */
    private void addIncludes(VelocityContext ctx) throws IllegalAccessException {
        Field[] fields = AddJobMojo.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getName().startsWith("include")) {
                ctx.put(field.getName(), Boolean.valueOf(field.getBoolean(this)));
            }
        }
    }

    /**
     * Create a String containing all the goals for a build. This includes
     * whatever goals are specified in the goals property and any goals required
     * by any enabled plugins.
     * 
     * @return a String containing all the goals necessary
     * @throws MojoExecutionException if something goes wrong in reflection
     */
    private String createFullGoals() throws MojoExecutionException {
        try {
            StringBuilder builder = new StringBuilder();
            if (shouldFailAtEnd) {
                builder.append("-fae ");
            }

            if (shouldUseBatchMode) {
                builder.append("-B ");
            }

            for (String plugin : PLUGINS) {
                if (isPluginEnabled(plugin) && PREFIXES.containsKey(plugin)) {
                    builder.append(StringUtils.join(PREFIXES.get(plugin).toArray(), " "));
                    builder.append(" ");
                }
            }

            if (performClean) {
                builder.append("clean ");
            }

            builder.append(primaryGoal);
            builder.append(" ");

            for (String plugin : PLUGINS) {
                if (isPluginEnabled(plugin) && GOALS.containsKey(plugin)) {
                    builder.append(StringUtils.join(GOALS.get(plugin).toArray(), " "));
                    builder.append(" ");
                }
            }

            if (additionalGoals != null) {
                builder.append(additionalGoals);
            }

            return builder.toString().trim();
        } catch (Exception e) {
            throw new MojoExecutionException("Exception while creating goals", e);
        }
    }

    /**
     * Reflect for a field named "include"+plugin (with the first character
     * upper cased) and return the value.
     * 
     * @param plugin the plugin name
     * @return if the plugin is enabled
     * @throws NoSuchFieldException if the field does not exist
     * @throws IllegalAccessException if we're unable to reflect
     */
    private boolean isPluginEnabled(String plugin) throws NoSuchFieldException,
            IllegalAccessException {
        String fieldName = "include" + plugin.substring(0, 1).toUpperCase() + plugin.substring(1);
        Field field = AddJobMojo.class.getDeclaredField(fieldName);
        return field.getBoolean(this);
    }

}
