package org.lastbamboo.maven.plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * Some utilities for implementing Mojos.
 */
public interface MojoUtils
    {
    /**
     * Returns the resolved main artifact of a given project.
     * 
     * @param project
     *      The project whose main artifact will be returned.
     * @param resolver
     *      The resolver used to resolve the artifact before returning.
     * @param remoteRepositories
     *      The list of remote repositories used for resolution.
     * @param localRepository
     *      The local repository used for resolution.
     *      
     * @return
     *      The resolved main artifact of the given project.
     *      
     * @throws MojoExecutionException
     *      If there is any problem resolving the artifact.
     */
    Artifact getMainArtifact
            (MavenProject project,
             ArtifactResolver resolver,
             List<ArtifactRepository> remoteRepositories,
             ArtifactRepository localRepository) throws MojoExecutionException;
    
    /**
     * Returns all of the artifacts of a given project, including the main
     * artifact.
     * 
     * @param project
     *      The project whose artifacts are returned.
     * @param resolver
     *      The resolver used to resolve the main artifact.
     * @param remoteRepositories
     *      The list of remote repositories used for resolution.
     * @param localRepository
     *      The local repository used for resolution.
     *      
     * @return
     *      All of the artifacts of the given project, including the main
     *      artifact.
     *      
     * @throws MojoExecutionException
     *      If there is a problem resolving the artifacts of the project.
     */
    Collection<Artifact> getArtifacts
            (MavenProject project,
             ArtifactResolver resolver,
             List<ArtifactRepository> remoteRepositories,
             ArtifactRepository localRepository) throws MojoExecutionException;
    
    /**
     * Changes the permissions of a file.
     * 
     * @param mode
     *      The permissions mode to set on the file.
     * @param file
     *      The file.
     *      
     * @throws MojoExecutionException
     *      If there is a problem setting the permissions of the file.
     */
    void chmod (String mode, File file) throws MojoExecutionException;
    
    /**
     * Merges a Velocity template with a context.
     * 
     * @param template The template.
     * @param vContext The context.
     * @param vComponent The component that provides the Velocity templating 
     * engine.
     * @param file The file to which to write the result of the merging.
     * @throws MojoExecutionException If there is a problem merging the 
     * template.
     */
    void mergeTemplate (String template, VelocityContext vContext,
         VelocityComponent vComponent, File file) throws MojoExecutionException;

    /**
     * Copies the artifacts of a given project into a given directory.
     * 
     * @param artifacts
     *      The artifacts to copy.
     * @param targetDir
     *      The directory.
     *      
     * @throws MojoExecutionException
     *      If there are any problems copying the artifacts.
     */
    void copyArtifacts(Collection<Artifact> artifacts, File targetDir) 
        throws MojoExecutionException;

    /**
     * Copies a file to a given directory.
     * 
     * @param file
     *      The file to copy.
     * @param targetDir
     *      The directory to which to copy the given file.
     *      
     * @throws MojoExecutionException
     *      If there is any problem copying the file.
     */
    public void copyFileToDirectory(File file, File targetDir) 
        throws MojoExecutionException;

    /**
     * Copies a directory.  The target directory will be created and have the
     * same contents as the source directory.  For example, if you have a
     * directory "foo" and call copyDirectory("foo", "bar"), a directory named
     * "bar" will be created with the same contents as "foo".
     * 
     * @param source
     *      The source directory.
     * @param target
     *      The target.
     *      
     * @throws MojoExecutionException
     *      If there is any problem copying the directory.
     */
    public void copyDirectory(File source, File target) 
        throws MojoExecutionException;

    /**
     * Copies a source file to the target file.
     * 
     * @param source 
     *     The source file.
     * @param target 
     *     The target file.
     * @throws MojoExecutionException
     *     If there's any problem copying the file.
     */
    void copyFile(File source, File target) throws MojoExecutionException;
    }
