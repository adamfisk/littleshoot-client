package org.lastbamboo.client.services.command;

import java.net.URI;

/**
 * Class containing data for download requests.
 */
public class DownloadCommand extends DownloadStreamCommand {

    private URI m_sha1Urn;

    private String m_mimeType;

    private String m_groupName;

    private String m_source;

    public void setUrn(final URI sha1) {
        this.m_sha1Urn = sha1;
    }

    public URI getUrn() {
        return m_sha1Urn;
    }

    public String getMimeType() {
        return this.m_mimeType;
    }

    public void setMimeType(final String mimeType) {
        m_mimeType = mimeType;
    }

    public void setGroupName(final String groupName) {
        m_groupName = groupName;
    }

    public String getGroupName() {
        return m_groupName;
    }

    public void setSource(final String source) {
        m_source = source;
    }

    public String getSource() {
        return m_source;
    }

    @Override
    public String toString() {
        return "Download for: " + getName() + " " + getUri() + " " + getSize()
                + " " + this.m_mimeType;
    }
}
