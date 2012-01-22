package org.lastbamboo.client.services;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.client.prefs.PrefKeys;
import org.lastbamboo.client.services.download.LibTorrentManager;
import org.lastbamboo.client.services.download.MoverDStateStatusVisitor;
import org.lastbamboo.common.download.DownloadVisitor;
import org.lastbamboo.common.download.Downloader;
import org.lastbamboo.common.download.GnutellaDownloader;
import org.lastbamboo.common.download.LittleShootDownloader;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.download.StreamableDownloader;
import org.lastbamboo.common.download.TorrentDownloader;
import org.lastbamboo.common.download.VisitableDownloader;
import org.lastbamboo.common.json.JsonUtils;
import org.littleshoot.util.Pair;
import org.lastbamboo.jni.JLibTorrent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor for representing downloads in JSON.
 */
public class JsonDownloadsVisitor
    {

    private static final int STOP_DOWNLOAD_DELETE_FILES = 1;
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final String m_escapedJson;
    private final FileMapper m_fileMapper;
    private final JLibTorrent m_libTorrent;
    
    /**
     * Creates a new visitor for converting search results to the JSON data 
     * format.
     * 
     * @param downloadTracker The class that keeps track of downloads.
     * @param resultsPerPage The number of results to include with each page.
     * @param pageIndex The index of the page to retrieve.
     * @param fileMapper The class for mapping files and SHA-1s.
     * @param downloadsDir The directory containing downloaded files.
     * @param libTorrentManager Manager for LibTorrent.
     */
    public JsonDownloadsVisitor(
        final int resultsPerPage, final int pageIndex,
        final FileMapper fileMapper, final File downloadsDir, 
        final LibTorrentManager libTorrentManager,
        final Collection<Entry<URI, Pair<Downloader<MoverDState<Sha1DState<MsDState>>>, VisitableDownloader<MsDState>>>> downloads)
        {
        this.m_fileMapper = fileMapper;
        final JSONObject json = new JSONObject();
        m_log.debug("Active downloads: "+downloads.size());
        JsonUtils.put(json, "total", downloads.size());
        JsonUtils.put(json, "downloadDir", downloadsDir.getAbsolutePath());

        m_libTorrent = libTorrentManager.getLibTorrent();
        m_libTorrent.updateSessionStatus();
        
        final Preferences prefs = Preferences.userRoot();
        final long historicUploadBytes = 
            prefs.getLong(PrefKeys.TOTAL_BITTORRENT_UPLOAD_BYTES, 0L);
        final long historicDownloadBytes = 
            prefs.getLong(PrefKeys.TOTAL_BITTORRENT_DOWNLOAD_BYTES, 0L);
        
        final JSONArray capabilities = new JSONArray();
        capabilities.put(STOP_DOWNLOAD_DELETE_FILES);
        try {
            json.put("capabilities", capabilities);
        } catch (final JSONException e) {
            m_log.error("Could not set capabilities!!", e);
        }
        JsonUtils.put(json, "historicUploadBytes", historicUploadBytes);
        JsonUtils.put(json, "historicDownloadBytes", historicDownloadBytes);
        JsonUtils.put(json, "totalUploadBytes", m_libTorrent.getTotalUploadBytes());
        JsonUtils.put(json, "totalDownloadBytes", m_libTorrent.getTotalDownloadBytes());
        JsonUtils.put(json, "totalPayloadUploadBytes", m_libTorrent.getTotalPayloadUploadBytes());
        JsonUtils.put(json, "totalPayloadDownloadBytes", m_libTorrent.getTotalPayloadDownloadBytes());
        JsonUtils.put(json, "uploadRate", m_libTorrent.getUploadRate());
        JsonUtils.put(json, "downloadRate", m_libTorrent.getDownloadRate());
        JsonUtils.put(json, "payloadUploadRate", m_libTorrent.getPayloadUploadRate());
        JsonUtils.put(json, "payloadDownloadRate", m_libTorrent.getPayloadDownloadRate());
        JsonUtils.put(json, "numPeers", m_libTorrent.getNumPeers());
        
        //final File[] complete = downloadsDir.listFiles();
        final int startIndex = pageIndex * resultsPerPage;
        final JSONArray array = new JSONArray();
        int downloadsIndex = 0;
        if (startIndex < downloads.size())
            {
            for (final Entry<URI, Pair<Downloader<MoverDState<Sha1DState<MsDState>>>, VisitableDownloader<MsDState>>> entry : downloads)
                {
                if (downloadsIndex >= startIndex && array.length() < resultsPerPage)
                    {
                    addDownload(array, entry);
                    }
                if (downloadsIndex == resultsPerPage)
                    {
                    break;
                    }
                downloadsIndex++;
                }
            }
        
        int filesIndex = startIndex - downloads.size();
        if (filesIndex < 0)
            {
            filesIndex = 0;
            }
        
        // The following lists completed files found in the download directory.
        /*
        if (array.length() < resultsPerPage && filesIndex < complete.length)
            {
            Arrays.sort(complete, new Comparator<File>() 
                {
                public int compare(final File f1, final File f2)
                    {
                    final Long lm1 = new Long(f1.lastModified());
                    final Long lm2 = new Long(f2.lastModified());
                    
                    final int answer = lm1.compareTo(lm2);
                    return answer;
                    }
                    
                });
            }
        while (array.length() < resultsPerPage && filesIndex < complete.length)
            {
            final File curFile = complete[filesIndex];
            
            // This only adds it if it's mapped.
            addComplete(array, curFile);
            filesIndex++;
            }
            */
        JsonUtils.put(json, "numDownloads", array.length());
        try
            {
            json.put("downloads", array);
            }
        catch (final JSONException e)
            {
            m_log.warn("Could not append JSON!!", e);
            }
        
        final String jsonString = json.toString();
        
        // We need to escape single quotes from the titles because they're
        // interpreted in JavaScript as close quotes.  We do it here because
        // the JSON code will do its own escaping if we do it when inserting
        // into the map.
        this.m_escapedJson = jsonString.replaceAll("'", "\\\\'");
        }

    private boolean addComplete(final JSONArray array, final File file)
        {
        //m_log.debug("Adding complete..");
        
        //if (uri == null)
        if (true)
            {
            //m_log.debug("File not in mapper");
            return false;
            }
        final URI uri = this.m_fileMapper.getUri(file);
        final JSONObject downloadJson = new JSONObject();

        JsonUtils.put(downloadJson, "title", file.getName());
        JsonUtils.put(downloadJson, "uri", uri);
        JsonUtils.put(downloadJson, "id", normalizeId(uri.toASCIIString()));
        JsonUtils.put(downloadJson, "size", file.length());
        JsonUtils.put(downloadJson, "lastModified", new Long(file.lastModified()));
        JsonUtils.put(downloadJson, "path", file.getAbsolutePath());
        JsonUtils.put(downloadJson, MoverDStateStatusVisitor.DOWNLOAD_STATUS, 
            MoverDStateStatusVisitor.COMPLETE_STATE);
        
        array.put(downloadJson);
        return true;
        }

    private void addDownload(final JSONArray array,
        final Entry<URI, Pair<Downloader<MoverDState<Sha1DState<MsDState>>>, VisitableDownloader<MsDState>>> entry)
        {
        final JSONObject downloadJson = new JSONObject();
        final Pair<Downloader<MoverDState<Sha1DState<MsDState>>>, VisitableDownloader<MsDState>> pair = 
            entry.getValue();

        final Downloader<MoverDState<Sha1DState<MsDState>>> dl = pair.getFirst();
        
        JsonUtils.put(downloadJson, "title", dl.getFinalName());
        JsonUtils.put(downloadJson, "uri", entry.getKey().toASCIIString());
        JsonUtils.put(downloadJson, "id", normalizeId(entry.getKey().toASCIIString()));
        JsonUtils.put(downloadJson, "size", dl.getSize());
        JsonUtils.put(downloadJson, "lastModified", System.currentTimeMillis());
        
        // This was change in 1.0.  It used to be the full path to the file.
        JsonUtils.put(downloadJson, "path", dl.getCompleteFile().getParent());
        
        final VisitableDownloader<MsDState> visitable = pair.getSecond();
        final DownloadVisitor<Object> visitor = new DownloadVisitor<Object>()
            {
            public Object visitLittleShootDownloader(
                final LittleShootDownloader downloader)
                {
                visitStreamable(downloader);
                JsonUtils.put(downloadJson, "numFiles", 1);
                JsonUtils.put(downloadJson, "downloadSource", 0);
                return null;
                }
            
            public Object visitGnutellaDownloader(
                final GnutellaDownloader downloader)
                {
                visitStreamable(downloader);
                JsonUtils.put(downloadJson, "numFiles", 1);
                JsonUtils.put(downloadJson, "downloadSource", 1);
                return null;
                }

            public Object visitTorrentDownloader(
                final TorrentDownloader downloader)
                {
                visitStreamable(downloader);
                JsonUtils.put(downloadJson, "maxByte", 
                    downloader.getMaxContiguousByte());
                JsonUtils.put(downloadJson, "numFiles", downloader.getNumFiles());
                final int torrentState = downloader.getTorrentState();
                
                JsonUtils.put(downloadJson, "torrentState", torrentState);
                JsonUtils.put(downloadJson, "downloadSource", 2);
                return null;
                }

            private void visitStreamable(final StreamableDownloader downloader)
                {
                JsonUtils.put(downloadJson, "streamable", 
                    downloader.isStreamable());
                }
        
            };
        visitable.accept(visitor);
        
        
        final MoverDState<Sha1DState<MsDState>> state = dl.getState();
        //m_log.debug("Getting state from downloader: {}", dl);
        //m_log.debug("Visiting state: {}", state);
        state.accept (new MoverDStateStatusVisitor (downloadJson));
        
        array.put(downloadJson);
        }
    
    private String normalizeId(final String id)
        {
        // See http://www.w3.org/TR/REC-html40/types.html#type-name
        //
        // "ID and NAME tokens must begin with a letter ([A-Za-z]) and may 
        // be followed by any number of letters, digits ([0-9]), hyphens ("-"), 
        // underscores ("_"), colons (":"), and periods (".")."
        
        // We just make it simple and return the unique hash code.
        return String.valueOf(id.hashCode());
        }

    /**
     * Accessor for the JSON string.
     * 
     * @return The string for the visited resources in the JSON data format.
     */
    public String getJson()
        {
        return this.m_escapedJson;
        }
    }
