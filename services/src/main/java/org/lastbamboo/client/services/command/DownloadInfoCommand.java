package org.lastbamboo.client.services.command;

import java.net.URI;

/**
 * Bean containing data for a request for information about a single download.
 */
public class DownloadInfoCommand {

    private URI uri;

    public void setUri(final URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }
}
