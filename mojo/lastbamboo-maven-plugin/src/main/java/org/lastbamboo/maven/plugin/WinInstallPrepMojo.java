package org.lastbamboo.maven.plugin;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal that creates the Windows executable using JSmooth.<p>
 * 
 * This plugin is not designed for reuse.  It creates a LittleShoot-specific
 * installer script and may contain other properties specific to LittleShoot.
 *
 * @goal win-install-prep
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class WinInstallPrepMojo extends AbstractMojo
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
     * The location of the tray icon file.
     * 
     * @parameter expression="${basedir}/src/main/resources/littleshoot_logo_osx_16.png"
     * @required
     */
    private File m_trayIcon;
    
    /**
     * The OS X application bundle name (without extension).
     * 
     * @parameter alias=winDistDir expression="${project.build.directory}/win
     * @required
     */
    private File m_winDistDir;
    
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
    /*
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
        */

    /**
     * Constructs this Mojo.
     */
    public WinInstallPrepMojo ()
        {
        m_mojoUtils = new MojoUtilsImpl();
        }

    /**
     * {@inheritDoc}
     */
    public void execute () throws MojoExecutionException
        {
        m_winDistDir.mkdir();
        
        //final Collection<Artifact> artifacts =
        //    m_mojoUtils.getArtifacts(m_project, m_resolver,
        //        m_remoteRepositories, m_localRepository);
        
        //copyArtifacts(artifacts, m_winDistDir);
        
        //m_mojoUtils.copyFileToDirectory(this.m_keystoreFile, m_winDistDir);
        m_mojoUtils.copyFileToDirectory(this.m_log4jProperties, m_winDistDir);
        m_mojoUtils.copyFileToDirectory(this.m_trayIcon, m_winDistDir);
        
        final File webappDir = new File(m_winDistDir, "src/main/webapp");
        webappDir.mkdirs();
        }
    
    /*
    private void copyArtifacts (final Collection<Artifact> artifacts,
        final File targetDir) throws MojoExecutionException
        {
        for (final Artifact artifact : artifacts)
            {
            final File file = artifact.getFile();
            final String name = normalizeJarName(file.getName());
            if (StringUtils.isBlank(name))
                {
                System.out.println("Ignoring jar: "+file.getName());
                continue;
                }
            final File targetFile = new File(targetDir, name);
            try
                {
                FileUtils.copyFile(file, targetFile);
                }
            catch (final IOException e)
                {
                throw new MojoExecutionException
                    ("Error copying '" + artifact.getArtifactId() + "'");   
                }
            }
        }
        */
    }
