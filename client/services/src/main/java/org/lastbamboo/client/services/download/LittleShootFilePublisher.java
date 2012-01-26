package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.lastbamboo.client.services.FileMapper;
import org.lastbamboo.client.services.RemoteResourceRepository;
import org.lastbamboo.common.http.client.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Publisher callback class that handles publishing files, typically after
 * they're downloaded.
 */
public class LittleShootFilePublisher implements FilePublisher {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final FileMapper m_fileMapper;
    private final RemoteResourceRepository m_remoteRepository;
    private final URI m_uri;
    private final URI m_expectedUrn;

    public LittleShootFilePublisher(final FileMapper fileMapper,
            final RemoteResourceRepository remoteRepository, final URI uri,
            final URI expectedUrn) {
        this.m_fileMapper = fileMapper;
        this.m_remoteRepository = remoteRepository;
        this.m_uri = uri;
        this.m_expectedUrn = expectedUrn;
    }

    public String publish(final File file) {
        try {
            // We just publish the raw file here because, even
            // though we have the cookies and such from the initial
            // request, the user might not actually be logged in.
            final String response = m_remoteRepository.insertResource(file,
                    true, m_uri, m_expectedUrn);
            m_fileMapper.map(m_uri, file);

            final Runnable hashMapper = new Runnable() {
                public void run() {
                    m_log.debug("Publishing file locally using hash");
                    m_fileMapper.map(file);
                }
            };
            final Thread hashThread = new Thread(hashMapper,
                    "Downloaded-File-Hashing-Thread");
            hashThread.setDaemon(true);
            hashThread.start();
            return response;
        } catch (final IOException e) {
            m_log.error("Could not publish downloaded file to remote "
                    + "repository", e);
            // TODO: Should we set the state to something else
            // here?
            return "";
        } catch (final ForbiddenException e) {
            m_log.error("Forbidden to publish to remote repository", e);
            // TODO: Should we set the state to something else
            // here?
            return "";
        }
    }

}
