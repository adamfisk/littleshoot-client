package org.lastbamboo.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * Goal which creates an OS X application for the Little Shoot application.
 * This could eventually be generalized to handle any application, but there are
 * currently some Little Shoot specific bits.
 *
 * @goal osxapp
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class OsxAppMojo extends AbstractMojo
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
     * The component that provides the engine for Velocity.  The Velocity
     * templating engine is used to generate the Info.plist for the
     * application bundle.
     * 
     * @component
     * @readonly
     */
    private VelocityComponent m_velocity;
    
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
     * The location of the Java application stub used to create Java application
     * bundles.
     * 
     * @parameter expression="/System/Library/Frameworks/JavaVM.framework/Versions/Current/Resources/MacOS/JavaApplicationStub"
     * @required
     */
    private File m_javaAppStub;
    
    /**
     * The Info.plist template.
     * 
     * @parameter alias="infoPlistTemplate" expression="Info.plist.template"
     * @required
     */
    private String m_infoPlistTemplate;
    
    /**
     * The icon of the application.
     * 
     * @parameter alias="iconFile"
     */
    private File m_iconFile;

    /**
     * The location of the webapp directory.
     * 
     * @parameter expression="${basedir}/src/main/webapp"
     * @required
     */
    //private File m_webappDir;
    
    /**
     * The location of the resources directory.
     * 
     * @parameter expression="${basedir}/src/main/resources"
     * @required
     */
    private File m_resourcesDir;
    
    /**
     * The location of the keystore file.
     * 
     * @parameter expression="${basedir}/littleShoot.keystore"
     * @required
     */
    private File m_keystoreFile;
    
    /**
     * The location of the log4j props file.
     * 
     * @parameter expression="${basedir}/../../install/bin/log4j.properties"
     * @required
     */
    private File m_log4jProperties;
    
    /**
     * The location of the single, combined jar.
     * 
     * @parameter expression="${basedir}/../../install/LittleShoot.jar"
     * @required
     */
    private File m_combinedJar;
    
    /**
     * The build location.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File m_projectBuildDir;
    
    /**
     * The bundle name of the resulting application bundle.
     * 
     * @parameter alias="bundleName"
     * @required
     */
    private String m_bundleName;
    
    /**
     * The class containing the application entry point.
     * 
     * @parameter alias="mainClass"
     * @required
     */
    private String m_mainClass;
    
    /**
     * The version of the application.
     * 
     * @parameter alias="appVersion"
     */
    private String m_appVersion;

    
    /**
     * Returns the classpath string to be inserted into the Info.plist file.
     * 
     * @param artifacts The collection of artifacts to be placed on the 
     * classpath.
     *      
     * @return The classpath string to be inserted into the Info.plist file.
     */
    private static String getClasspathString(
        final Collection<Artifact> artifacts)
        {
        final StringBuilder cpBuilder = new StringBuilder();
        
        for (final Artifact artifact : artifacts)
            {
            final String name = artifact.getFile().getName();
            
            cpBuilder.append("                ");
            cpBuilder.append("<string>");
            cpBuilder.append("$JAVAROOT/");
            cpBuilder.append(name);
            cpBuilder.append("</string>");
            cpBuilder.append('\n');
            }
        
        return cpBuilder.toString();
        }
    
    /**
     * Returns the classpath string to be inserted into the Info.plist file.
     * 
     * @param artifacts The collection of artifacts to be placed on the 
     * classpath.
     *      
     * @return The classpath string to be inserted into the Info.plist file.
     */
    private static String getSingleClasspathString(final String name)
        {
        final StringBuilder cpBuilder = new StringBuilder();
        cpBuilder.append("                ");
        cpBuilder.append("<string>");
        cpBuilder.append("$JAVAROOT/");
        cpBuilder.append(name);
        cpBuilder.append("</string>");
        cpBuilder.append('\n');
        return cpBuilder.toString();
        }
    
    /**
     * Writes the Info.plist file that contains meta-information about the
     * application bundle we are building.
     * 
     * @param mojoUtils Mojo utilities.
     * @param template The template used to generate the Info.plist file.
     * @param artifacts The artifacts of the application.  The products of 
     * these artifacts need to be placed in the classpath stored in the 
     * Info.plist file.
     * @param bundleName The application bundle name.
     * @param mainClass The main class that runs the application.
     * @param iconFile The file that is the icon for the application.  This 
     * can be null, if no icon is desired.
     * @param vComponent The Velocity component used to construct the 
     * Info.plist file from the given template.
     * @param dir The directory in which to write the Info.plist file.
     * @param appVersion The version of the application.
     * @throws MojoExecutionException If there is any problem creating or 
     * writing the Info.plist file.
     */
    private void writeInfoPlist (final MojoUtils mojoUtils,
        final String template,
        //final Collection<Artifact> artifacts,
        final String bundleName, final String mainClass, 
        final VelocityComponent vComponent, final File dir,
        final String appVersion) throws MojoExecutionException
        {
        final VelocityContext vc = new VelocityContext();
        
        vc.put("bundleName", bundleName);
        vc.put("mainClass", mainClass);
        //vc.put("classpath", getClasspathString(artifacts));
        vc.put("classpath", getSingleClasspathString(m_combinedJar.getName()));
        vc.put("appVersion", appVersion);
        vc.put("CFBundleShortVersionString", 
            appVersionToCFBundleShortVersionString(appVersion));
        
        vc.put("CFBundleVersion", 
            appVersionToCFBundleVersion(appVersion));
        
        final File infoPlist = new File(dir, "Info.plist");
        
        mojoUtils.mergeTemplate(template, vc, vComponent, infoPlist);
        }
    
    private static Integer appVersionToCFBundleVersion(final String appVersion)
        {
        // This should technically be only incremented by 1 each time, but 
        // we just use the actual integer of the version.
        final String major = 
            StringUtils.substringBefore(appVersion, ".");
        final String minor = 
            StringUtils.substringAfter(appVersion, ".").substring(0, 1);
        final String minorMinor = 
            StringUtils.substringAfter(appVersion, ".").substring(1, 2);
        final String CFBundleVersion;
        if (major.equals("0"))
            {
            CFBundleVersion = minor + minorMinor;
            }
        else
            {
            CFBundleVersion = major + minor + minorMinor;
            }
        
        return new Integer(Integer.parseInt(CFBundleVersion));
        }

    private static String appVersionToCFBundleShortVersionString(
        final String appVersion)
        {
        if (StringUtils.isBlank(appVersion))
            {
            throw new IllegalArgumentException("Application version not set -- " +
                "use -DappVersion=1.11, for example, on the command line");
            }
        if (appVersion.length() < 4)
            {
            throw new IllegalArgumentException(
                "Mac bundles require a major version, minor version, " +
                "and build number, as in 1.11 You supplied: "+appVersion);
            }
        final String major = 
            StringUtils.substringBefore(appVersion, ".");
        final String minor = 
            StringUtils.substringAfter(appVersion, ".").substring(0, 1);
        final String minorMinor = 
            StringUtils.substringAfter(appVersion, ".").substring(1, 2);
        return major + "." + minor + "." + minorMinor;
        }

    /**
     * Copies the Java application stub to a given directory, making sure that
     * it is executable.
     * 
     * @param mojoUtils Mojo utilities.
     * @param javaAppStub The Java application stub.
     * @param dir The directory to which to copy the stub.
     * @throws MojoExecutionException If there is any problem copying the stub.
     */
    private void copyJavaAppStub (final MojoUtils mojoUtils,
        final File javaAppStub, final File dir) throws MojoExecutionException
        {
        final File bundleStub = new File(dir, m_bundleName);
        
        try
            {
            FileUtils.copyFile(javaAppStub, bundleStub);
            }
        catch (final IOException e)
            {
            throw new MojoExecutionException(
                "Error copying stub '" + javaAppStub.getName() + "'", e);
            }
        
        mojoUtils.chmod("755", bundleStub);
        }
    
    /**
     * Sets the bundle attribute on a given file.
     * 
     * @param file The file.
     *      
     * @throws MojoExecutionException If there is a problem setting the bundle 
     * attribute.
     */
    private static void setBundleAttribute (final File file) 
        throws MojoExecutionException
        {
        final Commandline setFile = new Commandline();
        
        try
            {
            setFile.setExecutable("/Developer/Tools/SetFile");
            setFile.createArgument().setValue("-a B");
            setFile.createArgument().setValue(file.getAbsolutePath());

            setFile.execute();
            }
        catch (final CommandLineException e)
            {
            throw new MojoExecutionException
                    ("Could not set bundle attribute on '" + file.getName() + 
                             "'", e);
            }
        }
    
    /**
     * Constructs this Mojo.
     */
    public OsxAppMojo ()
        {
        m_mojoUtils = new MojoUtilsImpl();
        }

    /**
     * {@inheritDoc}
     */
    public void execute () throws MojoExecutionException
        {
        //getLog().info(m_webappDir.getAbsolutePath());
        
        final File appBundle = new File(m_projectBuildDir, m_bundleName + ".app");
        appBundle.mkdir();
        
        final File contentsDir = new File(appBundle, "Contents");
        contentsDir.mkdir();
        
        final File resourcesDir = new File(contentsDir, "Resources");
        resourcesDir.mkdir();
        
        final File javaDir = new File(resourcesDir, "Java");
        javaDir.mkdir();
        
        /*
        final Collection<Artifact> artifacts =
                m_mojoUtils.getArtifacts(m_project,
                                         m_resolver,
                                         m_remoteRepositories,
                                         m_localRepository);
        
        m_mojoUtils.copyArtifacts(artifacts, javaDir);
        */
        try 
            {
            FileUtils.copyFileToDirectory(m_combinedJar, javaDir);
            } 
        catch (final IOException e) 
            {
            throw new MojoExecutionException("Could not copy combined jar", e);
            }
        
        if (this.m_keystoreFile.isFile())
            {
            m_mojoUtils.copyFileToDirectory(this.m_keystoreFile, javaDir);
            }
        
        // We switched to programmatic log4j configuration.
        //m_mojoUtils.copyFileToDirectory(this.m_log4jProperties, javaDir);
        
        final File macOsDir = new File(contentsDir, "MacOS");
        macOsDir.mkdir();
        
        final String appVersion;
        if (StringUtils.isNotBlank(System.getProperty("appVersion")))
            {
            appVersion = System.getProperty("appVersion");
            }
        else
            {
            appVersion = m_appVersion;
            }
        
        writeInfoPlist(m_mojoUtils,
                       m_infoPlistTemplate,
                       m_bundleName,
                       m_mainClass,
                       m_velocity,
                       contentsDir,
                       appVersion);
        
        copyJavaAppStub(m_mojoUtils, m_javaAppStub, macOsDir);

        setBundleAttribute(appBundle);
        
        final File trayIconFile = 
            new File(m_resourcesDir, "littleshoot_logo_osx_16.png");
        if (trayIconFile.isFile()) 
            {
            m_mojoUtils.copyFileToDirectory(trayIconFile, javaDir);
            }
        if (m_iconFile == null)
            {
            // The icon file was not set.  We do nothing to set up an icon.
            }
        else
            {
            m_mojoUtils.copyFileToDirectory(m_iconFile, resourcesDir);
            }
        }
    }
