package org.lastbamboo.common.searchers.youtube;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lastbamboo.common.rest.AbstractJsonRestResult;
import org.lastbamboo.common.rest.ElementProcessingException;
import org.lastbamboo.common.rest.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Bean containing data for an individual YouTube video.
 */
public class YouTubeJsonVideo extends AbstractJsonRestResult 
    {

    private static final Logger m_log = 
        LoggerFactory.getLogger(YouTubeJsonVideo.class);
    

    /**
     * Creates a youTube video bean from JSON data.
     */
    public YouTubeJsonVideo(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        super(jsonResult, getUri(jsonResult), 
            getThumbnailUrl(jsonResult), "youtube");
        }
    
    private static URI getThumbnailUrl(final JSONObject json) 
        throws URISyntaxException, ElementProcessingException
        {
        //System.out.println("\n\n\nGot JSON: \n"+json.toString(2));
        /*
         * "media$group":{"media$thumbnail":
         * [{"width":"130","height":"97","time":"00:07:43.500",
         * "url":"http://img.youtube.com/vi/jDDkNFiEm1A/2.jpg"}
         */
        try
            {
            final JSONObject mediaGroup = json.getJSONObject("media$group");
            //System.out.println("Got Media GROUP: "+mediaGroup);
            final JSONArray mediaThumbnails = 
                mediaGroup.getJSONArray("media$thumbnail");
            final JSONObject mediaThumbnail = mediaThumbnails.getJSONObject(0);
            final String url = mediaThumbnail.getString("url");
            
            //System.out.println("Returning thumb URL: "+url);
            
            // While we're here, purge some stuff to save memory!!
            mediaGroup.remove("media$description");
            mediaGroup.remove("media$keywords");
            
            json.remove("category");
            json.remove("content");

            //System.out.println("Cleaned JSON: \n"+json.toString(2));
            return new URI(url);
            }
        catch (final JSONException e)
            {
            throw new ElementProcessingException("Bad JSON?...."+json, e);
            }
        }
    
    private static URI getUri(final JSONObject jsonResult) 
        throws URISyntaxException, ElementProcessingException
        {
        /*
         * "link":[{"href":"http://www.youtube.com/watch?v=pvoEiBnpCc8","type":"text/html","rel":"alternate"},
         * {"href":"http://gdata.youtube.com/feeds/api/videos/pvoEiBnpCc8/responses","type":"application/atom+xml"
         */
        final JSONArray links;
        try
            {
            links = jsonResult.getJSONArray("link");
            }
        catch (final JSONException e)
            {
            throw new ElementProcessingException("Bad JSON?...."+jsonResult, e);
            }
        
        for (int i = 0; i < links.length(); i++)
            {
            final JSONObject link;
            try
                {
                link = links.getJSONObject(i);
                final String rel = link.getString("rel");
                if (rel.equalsIgnoreCase("alternate"))
                    {
                    final String fullLink = link.getString("href");
                    final String vid = StringUtils.substringAfter(fullLink, "v=");
                    final String linkStr = 
                        "http://www.youtube.com/v/"+vid+"&fmt=18";
                    
                    //System.out.println("VIDEO LINK: "+linkStr);
                    return new URI(linkStr);
                    }
                }
            catch (final JSONException e)
                {
                final String msg = 
                    "Could not read JSON result from: "+jsonResult;
                m_log.warn("Could not read JSON result from: "+jsonResult, e);
                throw new URISyntaxException("Bad JSON?", msg);
                }
            }
        throw new ElementProcessingException("Bad JSON?...."+jsonResult);
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
