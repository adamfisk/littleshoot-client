package org.lastbamboo.client.services.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.LongRange;
import org.apache.commons.lang.math.RandomUtils;
import org.lastbamboo.client.services.FileMapper;
import org.lastbamboo.client.services.RemoteResourceRepository;
import org.lastbamboo.common.download.AbstractDownloader;
import org.lastbamboo.common.download.DownloadVisitor;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.GnutellaDownloader;
import org.lastbamboo.common.download.LaunchFileDispatcher;
import org.lastbamboo.common.download.LaunchFileTracker;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.VisitableDownloader;
import org.lastbamboo.common.searchers.limewire.LimeWire;
import org.lastbamboo.common.searchers.limewire.LimeWireJsonResult;
//import org.limewire.collection.Range;
//import org.limewire.core.api.download.DownloadException;
//import org.limewire.io.Address;
import org.limewire.io.GUID;
import org.limewire.io.IpPort;
import org.limewire.io.IpPortSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.limegroup.gnutella.Downloader.DownloadState;
import com.limegroup.gnutella.RemoteFileDesc;
import com.limegroup.gnutella.downloader.DownloadListener;

/**
 * Downloader for downloading files from LimeWire.
 */
public class LimeWireDownloader extends AbstractDownloader<MsDState>
    implements Downloader<MsDState>, VisitableDownloader<MsDState>,
    GnutellaDownloader //DownloadListener
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final URI m_sha1;
    private final LimeWire m_limeWire;
    private final int m_size;

    private LaunchFileTracker m_launchFileTracker;

    private RandomAccessFile m_randomAccessFile;

    private File m_completeFile;

    private File m_incompleteFile;

    private final FileMapper m_fileMapper;

    private final RemoteResourceRepository m_remoteRepository;

    private volatile boolean m_started;

    private boolean m_complete;

    private com.limegroup.gnutella.Downloader m_downloader;

    private MsDState m_state = MsDState.IDLE;

    private final String m_finalName;

    private boolean m_stopped;

    private final boolean m_streamable;

    /**
     * Creates a new downloader for downloading from LimeWire.
     * @param completeFile 
     * 
     * @param sha1 The SHA-1 hash for the file.
     * @param size The expected size of the file.
     * @param limeWire The interface to LimeWire.
     * @param fileMapper The class for mapping the file in the local database.
     * @param remoteRepository The remote repository for file data.
     * @param streamable Whether or not this download is streamable.
     */
    public LimeWireDownloader(final File completeFile, final URI sha1,
        final long size, final LimeWire limeWire,
        final FileMapper fileMapper,
        final RemoteResourceRepository remoteRepository,
        final boolean streamable) {
        this.m_streamable = streamable;
        if (completeFile.exists()) {
            m_log.warn("File already exists: {}", completeFile);
            throw new IllegalArgumentException("File already exists: "
                    + completeFile);
        }
        this.m_fileMapper = fileMapper;
        this.m_remoteRepository = remoteRepository;
        this.m_completeFile = completeFile;
        this.m_sha1 = sha1;

        if (size > Integer.MAX_VALUE) {
            m_log.warn("Cannot support file size: {}", size);
        }
        this.m_size = (int) size;
        this.m_limeWire = limeWire;
        this.m_finalName = completeFile.getName();
    }

    public File getIncompleteFile() {
        return this.m_incompleteFile;
    }

    public long getSize() {
        return m_size;
    }

    public MsDState getState() {
        return this.m_state;
    }

    public boolean isStarted() {
        return this.m_started;
    }

    public void start() {
        if (this.m_started) {
            m_log.debug("Already started...");
            return;
        }
        m_started = true;
        download(this.m_sha1);
    }

    public void write(final OutputStream os, final boolean cancelOnStreamClose)
        {
        m_log.debug("Writing file...");
        if (m_complete)
            {
            try
                {
                writeCompleteFile(os);
                }
            catch (final IOException e)
                {
                m_log.debug("Exception writing complete file", e);
                }
            }
        else
            {
            synchronized (this)
                {
                if (this.m_launchFileTracker == null)
                    {
                    try
                        {
                        this.wait(40000);
                        }
                    catch (final InterruptedException e)
                        {
                        m_log.error("Exception while waiting", e);
                        }
                    }
                }
            if (this.m_launchFileTracker == null)
                {
                m_log.error("Still no file tracker!!");
                return;
                }
            
            // This should generally be consistent with MultiSourceDownlaoder.
            try
                {
                m_launchFileTracker.write (os, cancelOnStreamClose);
                }
            catch (final IOException e)
                {
                m_log.debug("Caught exception during write", e);
                // This will typically be an exception from the servlet 
                // container indicating the user has closed the browser window, 
                // such as a  Jetty EofException.  Other cases can also 
                // cause this however.  A great example is Safari QuickTime 
                // files where Safari hands off downloading to QuickTime when
                // it gets a Content Type header for a file type QuickTime 
                // handles.  QuickTime will then send another HTTP request, 
                // and Safari will close the initial connection.  
                //
                // The key here is we should only cancel the download when 
                // this is the last active writer *and* the caller has specified
                // we should cancel the entire download when we've lost the
                // stream.  This is important to keep in mind -- an IO error
                // on the stream to the browser or whatever is very different
                // from a concrete indication we should cancel the download!!
                if (m_launchFileTracker.getActiveWriteCalls() == 0 &&
                    cancelOnStreamClose)
                    {
                    m_log.debug("Stopping and removing files");
                    stop (true);
                    }
                }
            catch (final Throwable t)
                {
                m_log.error("Throwable writing file.", t);
                }
            }
        }

    private void setState (final MsDState state)
        {
        if (m_state != null && m_state.equals (state))
            {
            // Do nothing.  The state has not changed.
            //m_log.debug("No state change.  State remaining: {}", state);
            }
        else
            {
            m_state = state;
            //m_log.debug ("Setting state of " +this.m_finalName +" to "+ state);
            fireStateChanged (state);
            }
        }
    
    private void writeCompleteFile(final OutputStream os) throws IOException
        {
        InputStream is = null;
        try
            {
            is = new FileInputStream(this.m_completeFile);
            IOUtils.copy(is, os);
            }
        finally
            {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            }
        }
        
    public void onPendingClose(final DownloadState status)
        {
        m_log.debug("File about to close with status: {}", status);
        /*
        INITIALIZING,
        QUEUED,
        CONNECTING,
        DOWNLOADING,
        BUSY,
        COMPLETE,
        ABORTED,
        GAVE_UP,
        DISK_PROBLEM,
        WAITING_FOR_GNET_RESULTS,
        CORRUPT_FILE,
        REMOTE_QUEUED,
        HASHING,
        SAVING,
        WAITING_FOR_USER,
        WAITING_FOR_CONNECTIONS,
        ITERATIVE_GUESSING,
        QUERYING_DHT,
        IDENTIFY_CORRUPTION,
        RECOVERY_FAILED,
        PAUSED,
        INVALID,
        RESUMING,
        FETCHING
         */
        try 
            {
            switch (status)
                {
                case COMPLETE:
                    this.m_launchFileTracker.onFileComplete();
                    this.m_launchFileTracker.waitForLaunchersToComplete();
                    setState(MsDState.COMPLETE);
                    break;
                    
                case CONNECTING:
                    m_log.warn("Got connecting status??");
                    break;
                
                default: 
                    m_log.warn("Got other status: "+status);
                    setState(MsDState.COULD_NOT_DETERMINE_SOURCES);
                    break;
                }
            }
        catch (final Throwable t)
            {
            m_log.warn("Throwable updating state", t);
            }
        }

    public void onRangeRead(final Range range)
        {
        if (this.m_stopped)
            {
            m_log.debug("Download stopped");
            return;
            }
        
        // Both range classes are documented to be inclusive on both ends.
        try
            {
            final LongRange lr = new LongRange(range.getLow(), range.getHigh());
            this.m_launchFileTracker.onRangeComplete(lr);
            
            setState (newDownloadingState());
            }
        catch (final Throwable t)
            {
            m_log.warn("Caught throwable processing range", t);
            }
        }

    private MsDState.Downloading newDownloadingState()
        {
        return new MsDState.LimeWireDownloadingState (
            this.m_downloader.getAverageBandwidth(),
            this.m_downloader.getNumHosts(), this.m_downloader.getAmountRead(),
            getSize());
        }
    
    private RemoteFileDesc[] createRfdsAndAltsFromSearchResults(
        final LimeWireJsonResult results, final List<RemoteFileDesc> altList) {
        final RemoteFileDesc[] rfds = results.getAllRemoteFileDescs();
        Set<IpPort> alts = new IpPortSet();
        alts.addAll(results.getAlts());        
        
        // Iterate through RFDs and remove matching alts.
        // Also store the first SHA1 capable RFD for collecting alts.
        RemoteFileDesc sha1RFD = null;
        for(int i = 0; i < rfds.length; i++) {
            RemoteFileDesc next = rfds[i];
            if(next.getSHA1Urn() != null)
                sha1RFD = next;
            Address address = next.getAddress();
            // response alts are always only ip ports and no kind of other address
            // so it suffices to compare ip port instances of rfd addresses with the alt set
            // since other address types won't match anyways
            if (address instanceof IpPort) 
                alts.remove(address); // Removes an alt that matches the IpPort of the RFD
        }

        // If no SHA1 rfd, just use the first.
        if(sha1RFD == null)
            sha1RFD = rfds[0];
        
        // Now iterate through alts & add more rfds.
        for(IpPort next : alts) {
            altList.add(m_limeWire.getRemoteFileDescFactory().createRemoteFileDesc(sha1RFD, next));
        }
        
        return rfds;
    }    

    private void download(final URI sha1) {
        m_log.info("Downloading!!");
        final LimeWireJsonResult results = this.m_limeWire.getResults(sha1);
        if (results == null) {
            m_log.warn(sha1 + " not found");
            throw new IllegalArgumentException(sha1 + " not found.");
        }

        GUID guid = results.getQueryGUID();

        final List<RemoteFileDesc> altList = new ArrayList<RemoteFileDesc>();
        final RemoteFileDesc[] rfds = 
            createRfdsAndAltsFromSearchResults(results, altList);

        m_log.info("Created RFDs of length: "+rfds.length);
        try {
            downloadWithOverwritePrompt(rfds, altList, guid);
        } catch (final DownloadException e) {
            m_log.warn("Issue with the save location...", e);

            // We give it another shot modifying the name with a random
            // number in the name.
            final String newName = FilenameUtils.getBaseName(
                m_completeFile.getName()) + "-" +
                (RandomUtils.nextInt() % 10000) + "." + 
                FilenameUtils.getExtension(m_completeFile.getName());
            m_completeFile = new File(m_completeFile.getParentFile(), newName);

            try {
                downloadWithOverwritePrompt(rfds, altList, guid);
            } catch (final DownloadException e1) {
                m_log.error("Still can't download file!", e1);
                // throw new RuntimeIoException("Save location error: ", e);
                setState(MsDState.FAILED);
            }

        }
    }

    /**
     * Downloads the given files, prompting the user if the file already exists.
     * @param queryGUID the guid of the query you are downloading rfds for.
     * @param searchInfo The query used to find the file being downloaded.
     * @throws SaveLocationException 
     */
    private void downloadWithOverwritePrompt(final RemoteFileDesc[] rfds,
        final List<? extends RemoteFileDesc> alts, final GUID queryGUID) 
        throws DownloadException 
        {
        
        final boolean overwrite = false;
        m_log.debug("About to initiate download....");
        final File saveDir = this.m_completeFile.getParentFile();
        final String fileName = this.m_completeFile.getName();
        m_log.debug("About to set state of {}", this.m_finalName);
        setState (new MsDState.GettingSourcesImpl());
        this.m_downloader =
            this.m_limeWire.getDownloadServices().download(rfds, alts, 
                queryGUID, overwrite, saveDir, fileName);
        //setState (new MsDState.DownloadingImpl (0, rfds.length + alts.size(),0L));
        
        this.m_downloader.addDownloadListener(this);
        /*
        if (dl != null) {
            setAsDownloading(rfds);
            if (validateInfo(searchInfo) == QUERY_VALID)
                dl.setAttribute(SEARCH_INFORMATION_KEY, (Serializable)searchInfo.toMap());
        }
        */
        }

    public void onData(final File file, final RandomAccessFile raf, 
        final int numChunks)
        {
        m_log.debug("Received download data!!");
        this.m_randomAccessFile = raf;
        this.m_launchFileTracker = new LaunchFileDispatcher (file,
            m_randomAccessFile, numChunks, this.m_sha1);
        this.m_incompleteFile = file;
        synchronized (this)
            {
            this.notify();
            }
        }

    public void onComplete()
        {
        m_log.debug("Got file complete...");
        if (!this.m_completeFile.isFile())
            {
            m_log.error("Expected file not a file: {}", this.m_completeFile);
            return;
            }
        m_complete = true;
        
        /*
        try
            {
            // We just publish the raw file here because, even
            // though we have the cookies and such from the initial
            // request, the user might not actually be logged in.
            m_remoteRepository.insertResource(m_completeFile, true);
            m_fileMapper.map(m_completeFile);
            }
        catch (final IOException e)
            {
            m_log.error("Could not publish downloaded file to remote " +
                "repository", e);
            // TODO: Should we set the state to something else
            // here?
            }
        catch (final ForbiddenException e)
            {
            m_log.error("Forbidden to publish to remote repository", e);
            // TODO: Should we set the state to something else
            // here?
            }
            */
        }

    public String getFinalName()
        {
        return m_finalName;
        }

    public File getCompleteFile()
        {
        return this.m_completeFile;
        }

    public void stop(final boolean removeFiles)
        {
        if (this.m_stopped)
            {
            m_log.debug("Already stopped!");
            return;
            }

        this.m_stopped = true;
        this.m_downloader.stop();
        
        if (removeFiles)
            {
            m_log.debug("Removing all files in addition to stopping");
            final File file = this.m_downloader.getFile();
            if (file != null)
                {
                file.delete();
                }
            final File saveFile = this.m_downloader.getSaveFile();
            if (saveFile != null)
                {
                saveFile.delete();
                }
            }
        //this.m_launchFileTracker.onDownloadStopped();
        
        setState(MsDState.CANCELED);
        }
    
    public void pause()
        {
        setState(new MsDState.PausedImpl(newDownloadingState()));
        this.m_downloader.pause();
        }
    
    public void resume()
        {
        m_log.error("LimeWire downloads don't yet support resume.");
        this.m_downloader.resume();
        setState (newDownloadingState());
        }
    
    public boolean isStreamable()
        {
        return m_streamable;
        }

    public <T> T accept(final DownloadVisitor<T> visitor)
        {
        return visitor.visitGnutellaDownloader(this);
        }

    public long getBytesRead()
        {
        return this.m_downloader.getAmountRead();
        }
    }
