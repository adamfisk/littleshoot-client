package org.lastbamboo.maven.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * An implementation of the Mojo utilities interface.
 */
public final class MojoUtilsImpl implements MojoUtils
    {
    /**
     * {@inheritDoc}
     */
    public Artifact getMainArtifact
            (final MavenProject project,
             final ArtifactResolver resolver,
             final List<ArtifactRepository> remoteRepositories,
             final ArtifactRepository localRepository)
                throws MojoExecutionException
        {
        final Artifact mainArtifact = project.getArtifact();
        
        try
            {
            resolver.resolve(mainArtifact, remoteRepositories, localRepository);
            }
        catch(final ArtifactResolutionException e)
            {
            throw new MojoExecutionException("Main artifact did not resolve",
                                             e);
            }
        catch(final ArtifactNotFoundException e)
            {
            throw new MojoExecutionException("Main artifact not found", e);
            }
        
        return mainArtifact;
        }
    
    /**
     * {@inheritDoc}
     */
    // The QDox parser used by maven for extracting information about the mojo
    // does not handle annotations properly.  However, a newer version does.
    // When maven migrates to the newer version, this can be put in to handle
    // the unchecked cast of the artifacts set below.  2007_03_21:jjc
    // @SuppressWarnings("unchecked")
    public Collection<Artifact> getArtifacts
            (final MavenProject project,
             final ArtifactResolver resolver,
             final List<ArtifactRepository> remoteRepositories,
             final ArtifactRepository localRepository)
                 throws MojoExecutionException
        {
        final Collection<Artifact> artifacts = new LinkedList<Artifact>();
        
        final Artifact mainArtifact = getMainArtifact(project,
                                                      resolver,
                                                      remoteRepositories,
                                                      localRepository);
        
        artifacts.add(mainArtifact);
        artifacts.addAll((Collection<Artifact>) project.getArtifacts());
    
        return artifacts;
        }
    
    /**
     * {@inheritDoc}
     */
    public void chmod
            (final String mode,
             final File file) throws MojoExecutionException
        {
        final Commandline chmod = new Commandline();
        
        try
            {
            chmod.setExecutable("chmod");
            chmod.createArgument().setValue(mode);
            chmod.createArgument().setValue(file.getAbsolutePath());

            chmod.execute();
            }
        catch (final CommandLineException e)
            {
            throw new MojoExecutionException
                    ("Could not set permissions of '" + file.getName() + "'",
                     e);
            }
        }
    
    /**
     * {@inheritDoc}
     */
    public void mergeTemplate
            (final String template,
             final VelocityContext vContext,
             final VelocityComponent vComponent,
             final File file) throws MojoExecutionException
        {
        final String fileName = file.getName();
        
        try
            {
            final FileWriter writer = new FileWriter(file);
            
            try
                {
                final VelocityEngine engine = vComponent.getEngine();
                
                engine.mergeTemplate(template, "utf-8", vContext, writer);
                }
            catch (final ParseErrorException e)
                {
                throw new MojoExecutionException
                        ("Error parsing '" + template + "'", e);
                }
            catch (final ResourceNotFoundException e)
                {
                throw new MojoExecutionException
                        ("Could not find template '" + template + "'",
                         e);
                }
            catch (final Exception e)
                {
                throw new MojoExecutionException
                        ("Unknown error occurred merging template '" +
                                template + "'", e);
                }
            finally
                {
                writer.close();
                }
            }
        catch (final IOException e)
            {
            throw new MojoExecutionException
                    ("I/O problem creating '" + fileName + "'", e);
            }
        }
    
    public void copyArtifacts
            (final Collection<Artifact> artifacts,
             final File targetDir) throws MojoExecutionException
        {
        for (final Artifact artifact : artifacts)
            {
            try
                {
                FileUtils.copyFileToDirectory(artifact.getFile(),
                                              targetDir);
                }
            catch (final IOException e)
                {
                throw new MojoExecutionException
                        ("Error copying '" + artifact.getArtifactId() +
                                "'");   
                }
            }
        }
    
    public void copyFileToDirectory
            (final File file,
             final File targetDir) throws MojoExecutionException
        {
        try
            {
            FileUtils.copyFileToDirectory(file, targetDir);
            }
        catch (final IOException e)
            {
            throw new MojoExecutionException
                    ("Error copying '" + file.getName() + "'");   
            }
        }

    public void copyDirectory
        (final File source, 
         final File target) throws MojoExecutionException
        {
        try
            {
            FileUtils.copyDirectory(source, target);
            }
        catch (final IOException e)
            {
            throw new MojoExecutionException
                    ("Error copying '" + source.getName() + "'");   
            }
        }

    public void copyFile
        (final File source, final File target) throws MojoExecutionException
        {
        try
            {
            FileUtils.copyFile(source, target);
            }
        catch (final IOException e)
            {
            throw new MojoExecutionException
                    ("Error copying '" + source.getName() + "'");   
            }
        }
    }
