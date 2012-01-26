package org.lastbamboo.maven.plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * Goal that creates the Windows executable using JSmooth.<p>
 * 
 * This plugin is not designed for reuse.  It creates a LittleShoot-specific
 * installer script and may contain other properties specific to LittleShoot.
 *
 * @goal win-launcher
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class WinJSmoothLauncherMojo extends AbstractMojo
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
     * The launcher template.
     * 
     * @parameter alias="launcherTemplate" expression="LittleShoot.jsmooth.template"
     * @required
     */
    private String m_launcherTemplate;
    
    /**
     * The Java system properties, appended in the form -Dkey=value.
     * 
     * @parameter
     * @required
     * 
     * Note the field naming is different here, as at least maven 2.0.5 does
     * not seem to support aliases for Maps and Properties and possibly more.
     */
    private Properties javaSystemProps;
    
    /**
     * The script file name.
     * 
     * @parameter alias="runScriptName" expression="littleshoot.jsmooth"
     * @required
     */
    private String m_runScriptName;
    
    /**
     * The OS X application bundle name (without extension).
     * 
     * @parameter alias=winDistDir expression="${project.build.directory}/win
     * @required
     */
    private File m_winDistDir;
    
    /**
     * The class containing the application entry point.
     * 
     * @parameter alias="mainClass"
     * @required
     */
    private String m_mainClass;
    
    /**
     * The version string.
     * 
     * @parameter alias="appVersion"
     * @required
     */
    private String m_appVersion;
    
    /**
     * Writes the run script.
     * 
     * @param mojoUtils Mojo utilities.
     * @param template The template used to create the run script.
     * @param artifacts The list of artifacts needed to run the application.
     * @param mainClass The main class that runs the application.
     * @param systemProps The system properties to be set when writing the 
     * line that executes the JVM.
     * @param vComponent The velocity component used to access the Velocity 
     * templating engine.
     * @param scriptFile The script file to write.
     * @param installerName The name of the installer file to output.
     * @param appVersion The version of the application.
     * @throws MojoExecutionException If there are any problems creating the 
     * script.
     */
    private static void writeJSmoothScript(final MojoUtils mojoUtils,
        final String template, final Collection<Artifact> artifacts,
        final String mainClass, final Properties systemProps,
        final VelocityComponent vComponent, final File scriptFile,
        final String appVersion) throws MojoExecutionException
        {
        final VelocityContext vc = new VelocityContext();
        
        vc.put("mainClass", mainClass);
        vc.put("classpath", getClasspathStringJSmooth(artifacts));
        vc.put("javaProps", getJavaSystemPropsString(systemProps));
        vc.put("appVersion", appVersion);
        vc.put("exePath", "${EXECUTABLEPATH}");
        
        mojoUtils.mergeTemplate(template, vc, vComponent, scriptFile);
        }
    
    /**
     * Returns the Java system property string. 
     * 
     * @param systemProps  The {@link Map} of Java system properties.
     *      
     * @return The Java system properties string for JSmooth.
     */
    private static String getJavaSystemPropsString(final Properties systemProps)
        {
        /*
            Here's the format of JSmooth Java props:
           
            <javaProperties>
            <name>COMPUTERNAME</name>
            <value>${COMPUTERNAME}</value>
            </javaProperties>
         */
        final StringBuilder sb = new StringBuilder();
        final Set<Entry<Object, Object>> entries = systemProps.entrySet();
        for (final Entry<Object, Object> entry : entries)
            {
            sb.append("<javaProperties>\n");
            sb.append("<name>");
            sb.append(entry.getKey());
            sb.append("</name>\n");
            sb.append("<value>");
            sb.append(entry.getValue());
            sb.append("</value>\n");
            sb.append("</javaProperties>\n\n");
            }
        return sb.toString();
        }
    
    /**
     * Returns the classpath string to be inserted into the JSmooth launcher
     * script.
     * 
     * @param artifacts The collection of artifacts to be placed on the 
     * classpath.
     *      
     * @return The classpath string to be inserted into the JSmooth script file.
     * @throws MojoExecutionException If the classpath string is too long.
     */
    private static String getClasspathStringJSmooth(
        final Collection<Artifact> artifacts) throws MojoExecutionException
        {
        final StringBuilder cpBuilder = new StringBuilder();
        cpBuilder.append("<classPath>");
        cpBuilder.append("LittleShoot.jar");
        cpBuilder.append("</classPath>\n");
        
        return cpBuilder.toString();
        
        /*
        int length = 0;
        for (final Artifact artifact : artifacts)
            {
            String name = artifact.getFile().getName();
            name = normalizeJarName(name);
            if (StringUtils.isBlank(name))
                {
                System.out.println("Ignoring jar: "+
                    artifact.getFile().getName());
                continue;
                }
            cpBuilder.append("<classPath>");
            cpBuilder.append(name);
            cpBuilder.append("</classPath>\n");
            length += name.length();
            }
        
        // We check the length because Windows cuts off command lines after
        // a certain number of characters.
        final int maxLength = 880;
        if (length > maxLength)
            {
            final int over = length - maxLength;
            throw new MojoExecutionException("Classpath string "+over+ 
                " chars too long for exe at "+length+". string is:\n"+
                cpBuilder.toString());
            }
        return cpBuilder.toString();
        */
        }
    
    private static int s_jarCounter = 0;
    
    private static Map<String, String> s_jarNameMap = 
        new ConcurrentHashMap<String, String>();
    
    /**
     * Makes jar names smaller because the classpath gets too long on Windows,
     * cutting off some jars from the classpath.  
     * 
     * @param jarName The original name of the jar.
     * @return The shortened name of the jar, or the same name if there's no
     * standard shorter version.
     */
    private static String normalizeJarName(final String jarName)
        {
        if (jarName.contains("osx"))
            {
            return null;
            }
        
        if (s_jarNameMap.containsKey(jarName))
            {
            return s_jarNameMap.get(jarName);
            }
        else
            {
            s_jarCounter++;
            final String name = s_jarCounter + ".jar";
            s_jarNameMap.put(jarName, name);
            return name;
            }
        }

    /**
     * Constructs this Mojo.
     */
    public WinJSmoothLauncherMojo ()
        {
        m_mojoUtils = new MojoUtilsImpl();
        }

    /**
     * {@inheritDoc}
     */
    public void execute () throws MojoExecutionException
        {
        m_winDistDir.mkdir();
        
        final File scriptFile = new File(m_winDistDir, m_runScriptName);
        
        final Collection<Artifact> artifacts =
            m_mojoUtils.getArtifacts(m_project,
                                     m_resolver,
                                     m_remoteRepositories,
                                     m_localRepository);
        
        writeJSmoothScript(m_mojoUtils,
                m_launcherTemplate,
                artifacts,
                m_mainClass,
                javaSystemProps,
                m_velocity,
                scriptFile,
                m_appVersion);
        }
    }
