package org.lastbamboo.maven.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * Goal which touches a timestamp file.  NOTE: We removed the dependency
 * on HTTP client utils because it broke the build.  If we need this again,
 * we might have to code it with just straight HTTP client code, also 
 * swapping in manual EC2-specific checks.
 *
 * @goal runscript
 * @phase package
 * @requiresDependencyResolution runtime
 */
public final class RunScriptMojo extends AbstractMojo
    {
    /**
     * Mojo utilities.
     */
    private final MojoUtils m_mojoUtils;
    
    /**
     * The artifact resolver used to manually resolve artifacts.
     * 
     * @component
     * @readonly
     */
    private ArtifactResolver m_resolver;
    
    /**
     * The local artifact repository.
     * 
     * @parameter expression="${localRepository}"
     * @required
     * @readOnly
     */
    private ArtifactRepository m_localRepository;
    
    /**
     * The list of remote artifact repositories.
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     * @readOnly
     */
    private List<ArtifactRepository> m_remoteRepositories;
    
    /**
     * The list of arguments to pass to java.  These will each be separated
     * with a space.  Java arguments include "-server", for example.  
     * Classpath and -D arguments are handled separately.
     * 
     * @parameter 
     */
    private String[] javaArgs;
    
    /** 
     * The project itself.  We use this to traverse the dependencies when
     * building the application.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly 
     */ 
    private MavenProject m_project;
    
    /**
     * The component that provides the engine for Velocity.  The Velocity
     * templating engine is used to generate the script.
     * 
     * @component
     * @readonly
     */
    private VelocityComponent m_velocity;
    
    /**
     * The script template.
     * 
     * @parameter alias="scriptTemplate" expression="run.sh.template"
     * @required
     */
    private String m_scriptTemplate;
    
    /**
     * The class containing the application entry point.
     * 
     * @parameter alias="mainClass"
     * @required
     */
    private String m_mainClass;
    
    /**
     * The Java system properties, appended in the form -Dkey=value.
     * 
     * @parameter
     * 
     * Note the field naming is different here, as at least maven 2.0.5 does
     * not seem to support aliases for Maps and Properties and possibly more.
     */
    private Properties javaSystemProps;
    
    /**
     * The script file to write.
     * 
     * @parameter alias="scriptFile" expression="run.sh"
     * @required
     */
    private File m_scriptFile;
    
    /**
     * Returns the classpath string to be inserted into the run script
     * 
     * @param artifacts
     *      The collection of artifacts to be placed on the classpath.
     *      
     * @return
     *      The classpath string to be inserted into the Info.plist file.
     */
    private static String getClasspathString
            (final Collection<Artifact> artifacts)
        {
        final StringBuilder cpBuilder = new StringBuilder();
        
        for (final Artifact artifact : artifacts)
            {
            final String path = artifact.getFile().getAbsolutePath();
            
            cpBuilder.append(path);
            cpBuilder.append(File.pathSeparator);
            cpBuilder.append("\\\n");
            }
        
        return cpBuilder.toString();
        }
    
    /**
     * Returns the Java system property string. 
     * 
     * @param systemProps The {@link Map} of Java system properties.
     *      
     * @return The Java system properties string, in the form:<p>
     *      -Dkey=value 
     */
    private static String getJavaSystemPropsString (
        final Properties systemProps)
        {
        if (systemProps == null)
            {
            return "";
            }
        final StringBuilder sb = new StringBuilder();
        final Set<Entry<Object, Object>> entries = systemProps.entrySet();
        for (final Entry<Object, Object> entry : entries)
            {
            appendSystemProp(sb, entry.getKey(), entry.getValue());
            }

        // We need to look this up programmatically from Amazon.
        if (onEc2())
            {
            //appendSystemProp(sb, "java.rmi.server.hostname", 
              //  getPublicAddress().getCanonicalHostName());
            }
        return sb.toString();
        }
    
    /**
     * Accesses the public address for the EC2 instance.  This is necessary
     * because InetAddress.getLocalHost() will yield the private, NATted
     * address.
     * 
     * @return The public address for the EC2 instance, or <code>null</code> if
     * there's an error accessing the address.
     */
    /*
    public static InetAddress getPublicAddress()
        {
        // First just check if we're even on Amazon -- we could be testing
        // locally, for example.
        
        // Check to see if we're running on EC2.  If we're not, we're probably 
        // testing.  This technique could be a problem if the EC2 internal 
        // addressing is ever different from 10.253.
        try
            {
            if (!onEc2())
                {
                // Not running on EC2.  We might be testing, or this might be
                // a server running on another system.
                return NetworkUtils.getLocalHost();
                }
            }
        catch (final UnknownHostException e)
            {
            return null;
            }
        final String url = "http://169.254.169.254/latest/meta-data/public-ipv4";
        final DefaultHttpClient client = new DefaultHttpClientImpl();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(
            20 * 1000);
        final GetMethod method = new GetMethod(url);
        try
            {
            final int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK)
                {
                return null;
                }
            final String host = method.getResponseBodyAsString();
            return InetAddress.getByName(host);
            }
        catch (final HttpException e)
            {
            return null;
            }
        catch (final IOException e)
            {
            return null;
            }
        finally 
            {
            method.releaseConnection();
            }
        }
        */
    
    /**
     * Returns whether or not we're running on EC2.
     * 
     * @return <code>true</code> if we're running on EC2, otherwise 
     * <code>false</code>.
     */
    private static boolean onEc2()
        {
        /*
        // Good enough for now to determine if we're running on EC2.
        try
            {
            return NetworkUtils.getLocalHost().getHostAddress().startsWith("10.25");
            }
        catch (final UnknownHostException e)
            {
            return false;
            }
            */
        return false;
        }
    
    
    
    private static void appendSystemProp(final StringBuilder sb, 
        final Object key, final Object value)
        {
        sb.append("-D");
        sb.append(key);
        sb.append("=");
        sb.append(value);
        sb.append(" \\\n");
        }

    /**
     * Returns the Java system property string. 
     * 
     * @param systemProps 
     *      The {@link Map} of Java system properties.
     *      
     * @return 
     *      The Java arguments string.  This is on a single line, as in:
     *      -server or -enableassertions
     */
    private static String getJavaArgsString
            (final List<String> javaArgs)
        {
        final StringBuilder sb = new StringBuilder();
        for (final String arg : javaArgs)
            {
            sb.append(arg);
            sb.append(" ");
            }
        return sb.toString();
        }
    
    /**
     * Writes the run script.
     * 
     * @param mojoUtils
     *      Mojo utilities.
     * @param template
     *      The template used to create the run script.
     * @param artifacts
     *      The list of artifacts needed to run the application.
     * @param javaArgs 
     *      The list of arguments to pass to java.
     * @param mainClass
     *      The main class that runs the application.
     * @param systemProps
     *      The system properties to be set when writing the line that executes
     *      the JVM.
     * @param vComponent
     *      The velocity component used to access the Velocity templating
     *      engine.
     * @param scriptFile
     *      The script file to write.
     *      
     * @throws MojoExecutionException
     *      If there are any problems creating the script.
     */
    private static void writeScript
            (final MojoUtils mojoUtils,
             final String template,
             final Collection<Artifact> artifacts,
             final List<String> javaArgs, 
             final String mainClass,
             final Properties systemProps,
             final VelocityComponent vComponent,
             final File scriptFile)
                throws MojoExecutionException
        {
        final VelocityContext vc = new VelocityContext();
        
        vc.put("mainClass", mainClass);
        vc.put("javaArgs", getJavaArgsString(javaArgs));
        vc.put("classpath", getClasspathString(artifacts));
        vc.put("javaSystemProps", getJavaSystemPropsString(systemProps));
        
        // Use the special signifier for running from the command line --
        // typically this is just running from the repository.
        vc.put("appVersion", "0.00");
        
        mojoUtils.mergeTemplate(template, vc, vComponent, scriptFile);
        mojoUtils.chmod("770", scriptFile);
        }

    /**
     * Constructs this Mojo.
     */
    public RunScriptMojo ()
        {
        m_mojoUtils = new MojoUtilsImpl();
        }
    
    /**
     * {@inheritDoc}
     */
    public void execute () throws MojoExecutionException, MojoFailureException
        {
        final Collection<Artifact> artifacts =
                m_mojoUtils.getArtifacts(m_project,
                                         m_resolver,
                                         m_remoteRepositories,
                                         m_localRepository);
                
        
        final List<String> javaArgsToUse;
        if (javaArgs == null)
            {
            javaArgsToUse = Collections.emptyList();
            }
        else
            {
            javaArgsToUse = Arrays.asList(javaArgs);
            }
        writeScript(m_mojoUtils,
                    m_scriptTemplate,
                    artifacts,
                    javaArgsToUse,
                    m_mainClass,
                    javaSystemProps,
                    m_velocity,
                    m_scriptFile);
        }
    }
