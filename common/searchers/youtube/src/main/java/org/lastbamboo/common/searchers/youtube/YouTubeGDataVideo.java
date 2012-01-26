package org.lastbamboo.common.searchers.youtube;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.rest.AbstractRestResult;
import org.lastbamboo.common.rest.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Bean containing data for an individual YouTube video.
 */
public class YouTubeGDataVideo extends AbstractRestResult 
    implements YouTubeVideo
    {

    private static final Logger m_log = 
        LoggerFactory.getLogger(YouTubeGDataVideo.class);
    

    /**
     * Creates a youTube video bean from XML data.
     * 
     * @param entry The XML element to extract data from.
     * @throws URISyntaxException If there's any problem with the URIs contained
     * in the XML.
     */
    public YouTubeGDataVideo(final Element entry) throws URISyntaxException
        {
        super(createTitle(entry), createUri(entry), thumbUri(entry), 
            thumbWidth(entry), thumbHeight(entry), -1L, "youtube", null, null, 
            desc(entry), author(entry), length(entry), rating(entry));
        }
    
    private static float rating(final Element entry)
        {
        // Note: Not all videos have a rating.
        return NodeUtils.getFloatAttribute(entry, "gd:rating", "average");
        }

    private static int length(final Element entry)
        {
        final Element group = toGroup(entry);
        final NodeList durations = group.getElementsByTagName("yt:duration");
        final Element duration = (Element) durations.item(0);
        final String dur = duration.getAttribute("seconds");
        return Integer.parseInt(dur);
        }

    private static String author(final Element entry)
        {
        final NodeList authors = entry.getElementsByTagName("author");
        final Element author =  (Element) authors.item(0);
        return NodeUtils.createStringValue(author, "name");
        }

    private static String desc(final Element entry)
        {
        final Element group = toGroup(entry);
        final NodeList descs = group.getElementsByTagName("media:description");
        final Element desc = (Element) descs.item(0);
        return desc.getTextContent();
        }

    private static URI thumbUri(final Element entry) throws URISyntaxException
        {
        final Element thumb = toThumb(entry);
        final String url = thumb.getAttribute("url");
        return new URI(url);
        }

    private static int thumbWidth(final Element entry)
        {
        final Element thumb = toThumb(entry);
        final String dim = thumb.getAttribute("width");
        return Integer.parseInt(dim);
        }
    
    private static int thumbHeight(final Element entry)
        {
        final Element thumb = toThumb(entry);
        final String dim = thumb.getAttribute("height");
        return Integer.parseInt(dim);
        }
    
    private static Element toThumb(final Element entry)
        {
        final Element group = toGroup(entry);
        final NodeList thumbnails = 
            group.getElementsByTagName("media:thumbnail");
        final Element thumb = (Element) thumbnails.item(0);
        return thumb;
        }

    private static Element toGroup(final Element entry)
        {
        final NodeList groups = entry.getElementsByTagName("media:group");
        final Element group = (Element) groups.item(0);
        return group;
        }

    private static URI createUri(final Element entry) throws URISyntaxException
        {
        final NodeList links = entry.getElementsByTagName("link");
        for (int j = 0; j < links.getLength(); j++)
            {
            final Element link = (Element) links.item(j);
            final String rel = link.getAttribute("rel");
            if (rel.equalsIgnoreCase("alternate"))
                {
                final String fullLink = link.getAttribute("href");
                final String vid = StringUtils.substringAfter(fullLink, "v=");
                final String linkStr = 
                    "http://www.youtube.com/v/"+vid+"&fmt=18";
                return new URI(linkStr);
                }
            }
        throw new IllegalArgumentException("Could not find link in "+entry);
        }

    private static String createTitle(final Element entry)
        {
        final NodeList titles = entry.getElementsByTagName("title");
        return titles.item(0).getTextContent() + ".flv";
        }
    
    @Override
    public String toString() 
        {
        return getClass().getSimpleName()+ 
            " title: "+getTitle()+" at: "+getUrl();
        }

    }
