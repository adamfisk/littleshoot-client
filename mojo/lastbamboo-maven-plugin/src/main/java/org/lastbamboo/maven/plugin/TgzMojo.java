package org.lastbamboo.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * Goal which bundles an application into the appropriate directories for 
 * creating a TGZ.  It creates a script for running the application but does 
 * not create the TGZ itself, leaving that task to external scripts.
 *
 * @goal tgz
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class TgzMojo extends AbstractMojo
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
     * The script template.
     * 
     * @parameter alias="scriptTemplate" expression="run.sh.template"
     * @required
     */
    private String m_scriptTemplate;
    
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
     * @parameter alias="runScriptName" expression="run.sh"
     * @required
     */
    private String m_runScriptName;
    
    /**
     * The location of the keystore file.
     * 
     * @parameter expression="${basedir}/littleShoot.keystore"
     * @required
     */
    private File m_keystoreFile;
    
    /**
     * The location of the log4j configuration file.
     * 
     * @parameter expression="${basedir}/src/main/resources/log4j.properties"
     * @required
     */
    private File m_log4jProperties;
    
    /**
     * The name of the tgz directory.
     * 
     * @parameter expression="${project.build.directory}/tgz"
     * @required
     */
    private File m_tgzDir;
    
    /**
     * The version string.
     * 
     * @parameter alias="appVersion"
     * @required
     */
    private String m_appVersion;
    
    /**
     * The class containing the application entry point.
     * 
     * @parameter alias="mainClass"
     * @required
     */
    private String m_mainClass;
    
    /**
     * The location of the TGZ README file.
     * 
     * @parameter expression="${basedir}/src/main/resources/README_tgz"
     * @required
     */
    private File m_tgzReadMe;
    
    /**
     * The location of the webapp directory.
     * 
     * @parameter expression="${basedir}/src/main/webapp"
     * @required
     */
    private File m_webappDir;
    
    /**
     * Writes the run script.
     * 
     * @param mojoUtils
     *      Mojo utilities.
     * @param template
     *      The template used to create the run script.
     * @param artifacts
     *      The list of artifacts needed to run the application.
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
     * @param appVersion 
     *      The version of the application.
     * @throws MojoExecutionException
     *      If there are any problems creating the script.
     */
    private static void writeScript
            (final MojoUtils mojoUtils,
             final String template,
             final Collection<Artifact> artifacts,
             final String mainClass,
             final Properties systemProps,
             final VelocityComponent vComponent,
             final File scriptFile, final String appVersion)
                throws MojoExecutionException
        {
        final VelocityContext vc = new VelocityContext();
        
        vc.put("mainClass", mainClass);
        vc.put("javaArgs", "");
        vc.put("classpath", getClasspathString(artifacts));
        vc.put("javaSystemProps", getJavaSystemPropsString(systemProps));
        vc.put("appVersion", appVersion);
        
        mojoUtils.mergeTemplate(template, vc, vComponent, scriptFile);
        mojoUtils.chmod("770", scriptFile);
        }
    
    /**
     * Returns the Java system property string. 
     * 
     * @param systemProps 
     *      The {@link Map} of Java system properties.
     *      
     * @return 
     *      The Java system properties string, in the form:<p>
     * 
     *      -Dkey=value 
     */
    private static String getJavaSystemPropsString
            (final Properties systemProps)
        {
        final StringBuilder sb = new StringBuilder();
        final Set<Entry<Object, Object>> entries = systemProps.entrySet();
        for (final Entry<Object, Object> entry : entries)
            {
            sb.append("-D");
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append(" \\\n");
            }
        return sb.toString();
        }
    
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
            final String name = artifact.getFile().getName();
            cpBuilder.append("lib/");
            cpBuilder.append(name);
            cpBuilder.append(File.pathSeparator);
            cpBuilder.append("\\\n");
            }
        
        return cpBuilder.toString();
        }
    
    /**
     * Constructs this Mojo.
     */
    public TgzMojo
            ()
        {
        m_mojoUtils = new MojoUtilsImpl();
        }

    /**
     * {@inheritDoc}
     */
    public void execute
            () throws MojoExecutionException
        {
        if (!m_tgzDir.isDirectory() && !m_tgzDir.mkdir())
            {
            throw new MojoExecutionException(
                "Could not create tgz directory at: "+
                m_tgzDir.getAbsolutePath());
            }
        
        final File shootDir = new File(m_tgzDir, "LittleShoot-"+m_appVersion);
        try
            {
            FileUtils.deleteDirectory(shootDir);
            }
        catch (final IOException e)
            {
            throw new MojoExecutionException(
                "Could not delete shoot directory!");
            }
        
        if (!shootDir.isDirectory() && !shootDir.mkdir())
            {
            throw new MojoExecutionException(
                "Could not create new shoot directory -- exists: "+
                shootDir.exists());
            }
        
        final File libDir = new File(shootDir, "lib");
        libDir.mkdir();
        
        final File scriptFile = new File(shootDir, m_runScriptName);
        final File readMe = new File(shootDir, "README");
        
        final Collection<Artifact> artifacts =
                m_mojoUtils.getArtifacts(m_project,
                                         m_resolver,
                                         m_remoteRepositories,
                                         m_localRepository);
        
        m_mojoUtils.copyArtifacts(artifacts, libDir);
        
        m_mojoUtils.copyFileToDirectory(this.m_keystoreFile, shootDir);
        m_mojoUtils.copyFileToDirectory(this.m_log4jProperties, shootDir);
        m_mojoUtils.copyFile(this.m_tgzReadMe, readMe);
        
        final File webappDir = new File(shootDir, "src/main/webapp");
        webappDir.mkdirs();
        
        m_mojoUtils.copyDirectory(m_webappDir, webappDir);
            
        writeScript(m_mojoUtils,
                    m_scriptTemplate,
                    artifacts,
                    m_mainClass,
                    javaSystemProps,
                    m_velocity,
                    scriptFile,
                    m_appVersion);
        }
    }
