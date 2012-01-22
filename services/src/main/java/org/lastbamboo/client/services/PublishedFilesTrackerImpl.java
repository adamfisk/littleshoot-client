package org.lastbamboo.client.services;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.input.CountingInputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.json.JsonUtils;
import org.littleshoot.util.MimeType;
import org.littleshoot.util.ResourceTypeTranslator;
import org.littleshoot.util.ResourceTypeTranslatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks files that are still being hashed.
 */
public class PublishedFilesTrackerImpl implements PublishedFilesTracker
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final Map<File, FileData> m_files = 
        new ConcurrentHashMap<File, FileData>();
    
    public JSONObject getPublishedFiles(final MimeType mt)
        {
        final ResourceTypeTranslator translator = 
            new ResourceTypeTranslatorImpl();
        final JSONObject jsonObject = new JSONObject();
        synchronized (this.m_files)
            {
            final Collection<FileData> files = this.m_files.values();
            JsonUtils.put(jsonObject, "size", files.size());
            
            final JSONArray array = new JSONArray();
            for (final FileData fd : files)
                {
                final File file = fd.getFile();
                final String title = file.getName();
                final CountingInputStream cis = fd.getCis();
                final JSONObject json = new JSONObject();
                JsonUtils.put(json, "title", title);
                JsonUtils.put(json, "size", file.length());
                JsonUtils.put(json, "path", file.getAbsolutePath());
                JsonUtils.put(json, "mediaType", translator.getType(title));
                JsonUtils.put(json, "mimeType", mt.getMimeType(title));
                JsonUtils.put(json, "bytesHashed", cis.getByteCount());
                JsonUtils.put(json, "lastModified", file.lastModified());
                JsonUtils.put(json, "publishTime", fd.getPublishTime().getTime());
                
                final String status;
                if (cis.getByteCount() == file.length())
                    {
                    status = "Complete";
                    }
                else
                    {
                    status = "Calculating unique file hash...";
                    }
                JsonUtils.put(json, "status", status);
                
                final Map<String, String> params = fd.getParamMap();
                for (final Entry<String, String> entry : params.entrySet())
                    {
                    JsonUtils.put(json, entry.getKey(), entry.getValue());
                    }
                
                array.put(json);
                }
            try
                {
                jsonObject.put("files", array);
                }
            catch (final JSONException e)
                {
                m_log.error("Error accumulating JSON!!", e);
                }
            }
        return jsonObject;
        }

    public void addFile(final File file, final CountingInputStream cis,
        final Map<String, String> paramMap)
        {
        final FileData fd = new FileData(file, cis, paramMap);
        this.m_files.put(file, fd);
        }

    public void removeFile(final File file)
        {
        this.m_files.remove(file);
        }
    
    private static final class FileData 
        {

        private final File m_file;
        private final CountingInputStream m_cis;
        private final Date m_publishTime;
        private final Map<String, String> m_paramMap;

        private FileData(final File file, final CountingInputStream cis, 
            final Map<String, String> paramMap)
            {
            this.m_file = file;
            this.m_cis = cis;
            this.m_paramMap = paramMap;
            this.m_publishTime = new Date();
            }

        private File getFile()
            {
            return m_file;
            }

        private CountingInputStream getCis()
            {
            return m_cis;
            }

        private Date getPublishTime()
            {
            return m_publishTime;
            }

        private Map<String, String> getParamMap()
            {
            return m_paramMap;
            }
        }
    }
