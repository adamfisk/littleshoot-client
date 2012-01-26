package org.lastbamboo.common.jlibtorrent;

import java.net.URI;

public class LibTorrentDownloadListenerAdaptor 
    implements LibTorrentDownloadListener
    {

    public void onState(final int state)
        {
        }

    public void onFailure()
        {
        }

    public void onComplete()
        {
        }

	public void onVerifyStream(URI sha1) 
		{
		}
    }
