package org.lastbamboo.client.util.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proxy for the underlying launcher implementation.
 */
public final class LauncherProxy 
    {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LauncherProxy.class);
    
    /**
     * The underlying launching implementation.
     */
    private static Launcher s_launcherImpl;

    /**
     * Creates the launching implementation according to the operating
     * system.
     */
    static 
        {
		try 
			{
	        if (SystemUtils.IS_OS_WINDOWS)
	            {
	            s_launcherImpl = new WindowsLauncher();
	            }
	        else if (SystemUtils.IS_OS_MAC_OSX)
	            {
	            s_launcherImpl = new OSXLauncher();
	            }
			}
		catch (final Throwable t)
			{
			LOG.error("Unexpected error creating launcher", t);
			}
        }
    
    /**
     * Launches the specified file on the underlying operating system.
     * 
     * @param file The file to launch.
     * @return Any int status code returned by the underlying operating system.
     * @throws IOException If there's any IO error launching the file.
     */
    public static int launchFile(final File file) throws IOException
        {
        final String name = file.getName();
        LOG.debug("Launching file: "+name);
        if (FilenameUtils.isExtension(name, "flv"))
            {
            LOG.debug("Creating YouTube file...");
            final File htmlFile = 
                createYouTubeHtmlFile(file.getParentFile().getParentFile(), 
                    file.toURI().toASCIIString(), name);
            return LauncherProxy.launchFile(htmlFile);
            }
        return s_launcherImpl.launchFile(file);
        }
    
    /**
     * Simply writes an html file with the YouTube URL embedded in the html. 
     * We cannot download content directly from YouTube, so this is the best
     * we can do.  HTTP client would otherwise download a page that would
     * say that it doesn't have Flash installed.
     * 
     * @param url The URL for the file.
     * @param fileName The name of the file.
     * @param session The browser session this download is associated with.
     * @param saveDir The directory for saving files.  We will just write 
     * the file directly to this directory.
     * @throws IOException If there's any error creating the file.
     */
    private static File createYouTubeHtmlFile(final File dir, final String url, 
        final String fileName) throws IOException
        {
        LOG.debug("Creating YouTube file...");
        final File newFile = new File(dir, fileName+".yt.html");
        final String html =
            "<object width=\"425\" height=\"350\"><param name=\"movie\" " +
            "value=\"" +
            url +
            "\"></param>" +
            "<embed src=\"" +
            url +
            "\" type=\"application/x-shockwave-flash\" width=\"425\" " +
            "height=\"350\"></embed></object>";
        /*
        final String html = 
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "+
            "\"http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd\">" +
            "<html>"+
            "<body onLoad=\"goToUrl();\">"+
            "<script type =\"text/javascript\">"+
                "function goToUrl()"+
                    "{" +
                    "document.location.href=\"" +
                    url +
                    "\";"+
                    "}"+
            "</script>"+
            "</body>"+
            "</hmtl>";
            */
        
        OutputStream os = null;
        try 
            {
            os = new FileOutputStream(newFile);        
            os.write(html.getBytes("UTF-8"));
            return newFile;
            }
        finally 
            {
            IOUtils.closeQuietly(os);
            }
        }

    /**
     * Opens the specified URL in a web browser.
     * 
     * @param url The URL to open.
     * @return Any int status code returned by the underlying operating system.
     * @throws IOException If there's any IO error opening the URL.
     */
    public static int openUrl(final URI url) throws IOException
        {
        LOG.trace("Opening URL: "+url);
        return s_launcherImpl.openUrl(url);
        }

    }
