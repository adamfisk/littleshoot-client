package org.lastbamboo.common.sdp.api;

import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.sdp.MediaDescriptionImpl;
import org.lastbamboo.common.sdp.SessionDescriptionImpl;
import org.lastbamboo.common.sdp.TimeDescriptionImpl;
import org.lastbamboo.common.sdp.fields.AttributeField;
import org.lastbamboo.common.sdp.fields.BandwidthField;
import org.lastbamboo.common.sdp.fields.ConnectionField;
import org.lastbamboo.common.sdp.fields.EmailField;
import org.lastbamboo.common.sdp.fields.InformationField;
import org.lastbamboo.common.sdp.fields.KeyField;
import org.lastbamboo.common.sdp.fields.MediaField;
import org.lastbamboo.common.sdp.fields.OriginField;
import org.lastbamboo.common.sdp.fields.PhoneField;
import org.lastbamboo.common.sdp.fields.ProtoVersionField;
import org.lastbamboo.common.sdp.fields.RepeatField;
import org.lastbamboo.common.sdp.fields.SDPKeywords;
import org.lastbamboo.common.sdp.fields.SessionNameField;
import org.lastbamboo.common.sdp.fields.TimeField;
import org.lastbamboo.common.sdp.fields.URIField;
import org.lastbamboo.common.sdp.fields.ZoneField;
import org.lastbamboo.common.sdp.parser.SDPAnnounceParser;
import org.littleshoot.util.NetworkUtils;
/**
 * The SdpFactory enables applications to encode and decode SDP messages.
 * The SdpFactory can be used to construct a SessionDescription 
 * object programmatically.  
 * The SdpFactory can also be used to construct a 
 * SessionDescription based on the
 * contents of a String. Acknowledgement:
 * Bugs reported by Brian J. Collins <bjcollins@rockwellcollins.com>.
 * and by  Majdi Abuelbassal <majdi.abuelbassal@bbumail.com>.
 * Please refer to IETF RFC 2327 for a description of SDP.
 *
 *@author Olivier Deruelle <olivier.deruelle@nist.gov>
 *@author M. Ranganathan <mranga@nist.gov> 
 *<a href="{@docRoot}/uncopyright.html">This code is in the public domain.</a>
 *
 * @version 1.0
 *
 */
public class SdpFactory extends Object {

    private static final Logger LOG = LoggerFactory.getLogger(SdpFactory.class);
    
    // unused since findSessions() is no longer implemented
    protected static final List sessionDescriptionsList = 
        Collections.synchronizedList(new LinkedList());
    
    /** Obtain an instance of an SdpFactory.
     *
     *     This static method returns a factory instance.
     *
     *     Once an application has obtained a reference to an SdpFactory it can use
     * the factory to
     *     configure and obtain parser instances and to create SDP objects.
     * @throws SdpException
     * @return a factory instance
     */    
    public static SdpFactory getInstance()
        {
        return new SdpFactory();
        }
    
    /** Creates a new, empty SessionDescription. The session is set as follows:
     *
     *     v=0
     *
     *     o=this.createOrigin ("user",
     *     NetworkUtils.getLocalHost().toString());
     *
     *     s=-
     *
     *     t=0 0
     * @throws SdpException SdpException, - if there is a problem constructing the
     *          SessionDescription.
     * @return a new, empty SessionDescription.
     */    
    public SessionDescription createSessionDescription()
    throws SdpException {
        SessionDescriptionImpl 
	   sessionDescriptionImpl=new SessionDescriptionImpl();
        
        ProtoVersionField ProtoVersionField=new ProtoVersionField();
        ProtoVersionField.setVersion(0);
        sessionDescriptionImpl.setVersion(ProtoVersionField);
        
        OriginField originImpl=null;
        try{
            originImpl=(OriginField)this.createOrigin("user",
            NetworkUtils.getLocalHost().getHostAddress());
        }
        catch(UnknownHostException e) {e.printStackTrace();}
        sessionDescriptionImpl.setOrigin(originImpl);
        
        SessionNameField sessionNameImpl=new SessionNameField();
        sessionNameImpl.setValue("-");
        sessionDescriptionImpl.setSessionName(sessionNameImpl);
        
        TimeDescriptionImpl timeDescriptionImpl=new TimeDescriptionImpl();
        TimeField timeImpl=new TimeField();
        timeImpl.setZero();
        timeDescriptionImpl.setTime(timeImpl);
        Vector times=new Vector();
        times.addElement(timeDescriptionImpl);
        sessionDescriptionImpl.setTimeDescriptions(times);
        
        // Dan Muresan: this was a memory leak
        // sessionDescriptionsList.addElement(sessionDescriptionImpl);
        return sessionDescriptionImpl;
    }
    
    /** Creates a SessionDescription populated with the information
     *     contained within the string parameter.
     *
     *     Note: unknown field types should not cause exceptions.
     * @param s s - the sdp message that is to be parsed.
     * @throws SdpParseException SdpParseException - if there is a problem parsing the
     *          String.
     * @return a populated SessionDescription object.
     */    
    public SessionDescription createSessionDescription(String s)
        throws SdpParseException {
        try {
            final SDPAnnounceParser sdpParser = new SDPAnnounceParser(s);
            return sdpParser.parse();
        } catch (final ParseException e) {
            LOG.warn("Could not parse SDP", e);
            throw new SdpParseException(0,0, "Could not parse message", e);
        }
    }
    
    /** Returns Bandwidth object with the specified values.
     * @param modifier modifier - the bandwidth type
     * @param value the bandwidth value measured in kilobits per second
     * @return bandwidth
     */    
    public BandWidth createBandwidth(String modifier,
    int value) {
         BandwidthField bandWidthImpl=new BandwidthField();
        try {
           
            bandWidthImpl.setType(modifier);
            bandWidthImpl.setValue(value);
            
        }
        catch(SdpException e) {
            LOG.warn("Unexpected exception", e);
        }
        return bandWidthImpl;
    }
    
    /** Returns Attribute object with the specified values.
     * @param name the namee of the attribute
     * @param value the value of the attribute
     * @return Attribute
     */    
    public Attribute createAttribute(String name,
    String value) {
         AttributeField attributeImpl=new AttributeField();
        try {
           
            attributeImpl.setName(name);
            attributeImpl.setValue(value);
           
        }
        catch(SdpException e) {
            LOG.warn("Unexpected exception", e);
        }
         return attributeImpl;
    }
    
    /** Returns Info object with the specified value.
     * @param value the string containing the description.
     * @return Info
     */    
    public Info createInfo(String value) {
          InformationField infoImpl=new InformationField();
        try {
          
            infoImpl.setValue(value);
        
        }
        catch(SdpException e) {
            LOG.warn("Unexpected exception", e);
        }
            return infoImpl;
    }
    
    /** Returns Phone object with the specified value.
     * @param value the string containing the description.
     * @return Phone
     */    
    public Phone createPhone(String value) {
        PhoneField phoneImpl=new PhoneField();
        try {
            
            phoneImpl.setValue(value);
           
        }
        catch(final SdpException e) {
            LOG.warn("Unexpected exception", e);
        }
         return phoneImpl;
    }
    
    /** Returns EMail object with the specified value.
     * @param value the string containing the description.
     * @return EMail
     */    
    public EMail createEMail(String value) {
        EmailField emailImpl=new EmailField();
        try {
            
            emailImpl.setValue(value);
            
        }
        catch(final SdpException e) {
            LOG.warn("Unexpected exception", e);
        }
        return emailImpl;
    }
    
    /** Returns URI object with the specified value.
     * @param value the URL containing the description.
     * @throws SdpException
     * @return URI
     */    
    public org.lastbamboo.common.sdp.api.URI createURI(final URL value)
        {
        final URIField uriImpl = new URIField();
        uriImpl.set(value);
        return uriImpl;
        }
    
    /** Returns SessionName object with the specified name.
     * @param name the string containing the name of the session.
     * @return SessionName
     */    
    public SessionName createSessionName(final String name) {
        final SessionNameField sessionNameImpl = new SessionNameField();
        try {          
            sessionNameImpl.setValue(name);
        }
        catch (final SdpException e) {
            LOG.warn("Could not set value", e);
        }
        return sessionNameImpl;
    }
    
    /** Returns Key object with the specified value.
     * @param method the string containing the method type.
     * @param key the key to set
     * @return Key
     */    
    public Key createKey(final String method, final String key) {
         KeyField keyImpl=new KeyField();
        try {
           
            keyImpl.setMethod(method);
            keyImpl.setKey(key);
           
        }
        catch(final SdpException e) {
            LOG.warn("Unexpected exception", e);
	    return null;
        }
         return keyImpl;
    }
    
    /** Returns Version object with the specified values.
     * @param value the version number.
     * @return Version
     */    
    public Version createVersion(int value) {
        ProtoVersionField protoVersionField=new ProtoVersionField();
        try {
          
            protoVersionField.setVersion(value);
          
        }
        catch(final SdpException e) {
            LOG.warn("Unexpected exception", e);
	    return null;
        }
        return protoVersionField;
    }
    
    /** Returns Media object with the specified properties.
     * @param media the media type, eg "audio"
     * @param port port number on which to receive media
     * @param numPorts number of ports used for this media stream
     * @param transport transport type, eg "RTP/AVP"
     * @param staticRtpAvpTypes vector to set
     * @throws SdpException
     * @return Media
     */    
    public Media createMedia(String media,
                         int port,
                         int numPorts,
                         String transport,
                         Vector staticRtpAvpTypes)
                         throws SdpException {
            MediaField mediaImpl=new MediaField();
            mediaImpl.setMediaType(media);
            mediaImpl.setMediaPort(port);
            mediaImpl.setPortCount(numPorts);
            mediaImpl.setProtocol(transport);
            mediaImpl.setMediaFormats(staticRtpAvpTypes);
            return mediaImpl;
    }
    
    /** Returns Origin object with the specified properties.
     * @param userName the user name.
     * @param address the IP4 encoded address.
     * @throws SdpException if the parameters are null
     * @return Origin
     */    
    public Origin createOrigin(String userName,
                           String address)
                           throws SdpException {
            OriginField originImpl=new OriginField();
            originImpl.setUsername(userName);
            originImpl.setAddress(address);
            originImpl.setNetworkType(SDPKeywords.IN);
            originImpl.setAddressType(SDPKeywords.IPV4);
            return originImpl;
    }
    
    /** Returns Origin object with the specified properties.
     * @param userName String containing the user that created the
     *          string.
     * @param sessionId long containing the session identifier.
     * @param sessionVersion long containing the session version.
     * @param networkType String network type for the origin (usually
     *          "IN").
     * @param addrType String address type (usually "IP4").
     * @param address String IP address usually the address of the
     *          host.
     * @throws SdpException if the parameters are null
     * @return Origin object with the specified properties.
     */    
    public Origin createOrigin(String userName,
                           long sessionId,
                           long sessionVersion,
                           String networkType,
                           String addrType,
                           String address)
                           throws SdpException {
            OriginField originImpl=new OriginField();
            originImpl.setUsername(userName);
            originImpl.setAddress(address);
            originImpl.setSessionId(sessionId);
            originImpl.setSessionVersion(sessionVersion);
            originImpl.setAddressType(addrType);
            originImpl.setNetworkType(networkType);
            return originImpl;                 
    }
    
    /** Returns MediaDescription object with the specified properties.
     *     The returned object will respond to
     *     Media.getMediaFormats(boolean) with a Vector of media formats.
     * @param media media -
     * @param port port number on which to receive media
     * @param numPorts number of ports used for this media stream
     * @param transport transport type, eg "RTP/AVP"
     * @param staticRtpAvpTypes list of static RTP/AVP media payload
     *          types which should be specified by the returned MediaDescription 
     *   throws IllegalArgumentException if passed
     *          an invalid RTP/AVP payload type
     * @throws IllegalArgumentException
     * @throws SdpException
     * @return MediaDescription
     */    
    public MediaDescription createMediaDescription(String media,
                                               int port,
                                               int numPorts,
                                               String transport,
                                               int[] staticRtpAvpTypes)
                                        throws IllegalArgumentException,
                                        SdpException {
            MediaDescriptionImpl 
		mediaDescriptionImpl=new MediaDescriptionImpl();
            MediaField mediaImpl=new MediaField();
            mediaImpl.setMediaType(media);
            mediaImpl.setMediaPort(port);
            mediaImpl.setPortCount(numPorts);
            mediaImpl.setProtocol(transport);
            mediaDescriptionImpl.setMedia(mediaImpl);
	    // Bug fix contributed by Paloma Ortega.
	    Vector payload=new Vector();
 	    for (int i=0;i<staticRtpAvpTypes.length;i++)
   		payload.add(new Integer(staticRtpAvpTypes[i]).toString());
 	    mediaImpl.setMediaFormats(payload);
            return mediaDescriptionImpl;                                     
    }
    
    /** Returns MediaDescription object with the specified properties.
     *     The returned object will respond to
     *     Media.getMediaFormats(boolean) with a Vector of String objects
     *     specified by the 'formats argument.
     * @param media the media type, eg "audio"
     * @param port port number on which to receive media
     * @param numPorts number of ports used for this media stream
     * @param transport transport type, eg "RTP/AVP"
     * @param formats list of formats which should be specified by the
     *          returned MediaDescription
     * @return MediaDescription
     */    
    public MediaDescription createMediaDescription(String media,
                                               int port,
                                               int numPorts,
                                               String transport,
                                               String[] formats) {
       MediaDescriptionImpl mediaDescriptionImpl=new MediaDescriptionImpl();
        try{
           
            MediaField mediaImpl=new MediaField();
            mediaImpl.setMediaType(media);
            mediaImpl.setMediaPort(port);
            mediaImpl.setPortCount(numPorts);
            mediaImpl.setProtocol(transport);
            
            Vector formatsV = new Vector(formats.length);
            for(int i = 0; i < formats.length; i ++)
                formatsV.add(formats[i]);
            mediaImpl.setMediaFormats(formatsV);
            mediaDescriptionImpl.setMedia(mediaImpl);
        } catch(final SdpException e) {
            LOG.warn("Unexpected exception", e);
        }
        return mediaDescriptionImpl;                                      
    }
    
    /** Returns TimeDescription object with the specified properties.
     * @param t the Time that the time description applies to. Returns
     *          TimeDescription object with the specified properties.
     * @throws SdpException
     * @return TimeDescription
     */    
    public TimeDescription createTimeDescription(Time t)
    throws SdpException {
            TimeDescriptionImpl timeDescriptionImpl=new TimeDescriptionImpl();
            timeDescriptionImpl.setTime(t);
            return timeDescriptionImpl;     
    }
    
    /** Returns TimeDescription unbounded (i.e. "t=0 0");
     * @throws SdpException
     * @return TimeDescription unbounded (i.e. "t=0 0");
     */    
    public TimeDescription createTimeDescription()
    throws SdpException {
        TimeDescriptionImpl timeDescriptionImpl=new TimeDescriptionImpl();
        TimeField timeImpl=new TimeField();
        timeImpl.setZero();
        timeDescriptionImpl.setTime(timeImpl);
        return timeDescriptionImpl;     
    }
    
    /** Returns TimeDescription object with the specified properties.
     * @param start start time.
     * @param stop stop time.
     * @throws SdpException if the parameters are null
     * @return TimeDescription
     */    
    public TimeDescription createTimeDescription(Date start,
                                             Date stop)
                                             throws SdpException {
        TimeDescriptionImpl timeDescriptionImpl=new TimeDescriptionImpl();
        TimeField timeImpl=new TimeField();
        timeImpl.setStart(start);
        timeImpl.setStop(stop);
        timeDescriptionImpl.setTime(timeImpl);
        return timeDescriptionImpl;                                 
    }
    /** Returns a String containing the computed form for a
     *   multi-connection address. 
     *  Parameters:
     *     addr - connection address
     *    ttl - time to live (TTL) for multicast
     *     addresses
     *     numAddrs - number of addresses used by the
     *    connection 
     * Returns:
     *     a String containing the computed form for a
     *     multi-connection address.
     */
    public String formatMulticastAddress(String addr,
                                     int ttl,
                                     int numAddrs) {
        String res=addr+"/"+ttl+"/"+numAddrs;  
        return res;
    }
    
    /** Returns a Connection object with the specified properties a
     * @param netType network type, eg "IN" for "Internet"
     * @param addrType address type, eg "IP4" for IPv4 type addresses
     * @param addr connection address
     * @param ttl time to live (TTL) for multicast addresses
     * @param numAddrs number of addresses used by the connection
     * @return Connection
     */    
    public Connection createConnection(String netType,
                                   String addrType,
                                   String addr,
                                   int ttl,
                                   int numAddrs)throws SdpException {
       ConnectionField connectionImpl=new ConnectionField(); 
    
            connectionImpl.setNetworkType(netType);
            connectionImpl.setAddressType(addrType);
            connectionImpl.setAddress(addr);
    
        return connectionImpl;
    }
    
    /** Returns a Connection object with the specified properties and no
     *     TTL and a default number of addresses (1).
     * @param netType network type, eg "IN" for "Internet"
     * @param addrType address type, eg "IP4" for IPv4 type addresses
     * @param addr connection address
     * @throws SdpException if the parameters are null
     * @return Connection
     */    
    public Connection createConnection(String netType,
                                   String addrType,
                                   String addr)
                                   throws SdpException {
     ConnectionField connectionImpl=new ConnectionField();
     
            connectionImpl.setNetworkType(netType);
            connectionImpl.setAddressType(addrType);
            connectionImpl.setAddress(addr);
     
         return connectionImpl;
    }
    
    /** Returns a Connection object with the specified properties and a
     *     network and address type of "IN" and "IP4" respectively.
     * @param addr connection address
     * @param ttl time to live (TTL) for multicast addresses
     * @param numAddrs number of addresses used by the connection
     * @return Connection
     */    
    public Connection createConnection(final String addr, final int ttl,
        final int numAddrs) throws SdpException {
        final ConnectionField connectionImpl = new ConnectionField();  
        connectionImpl.setAddress(addr);
        return connectionImpl;
    }
    
    /** Returns a Connection object with the specified address. This is
     *     equivalent to
     *
     *        createConnection("IN", "IP4", addr);
     *
     * @param addr connection address
     * @throws SdpException if the parameter is null
     * @return Connection
     */    
    public Connection createConnection(final String addr) throws SdpException {
        // Bug report by Brian J. Collins
        return createConnection("IN","IP4",addr);          
    }
    
    /** Returns a Time specification with the specified start and stop
     *     times.
     * @param start start time
     * @param stop stop time
     * @throws SdpException if the parameters are null
     * @return a Time specification with the specified start and stop times.
     */    
    public Time createTime(final Date start, final Date stop)
        throws SdpException {
        final TimeField timeImpl = new TimeField();
        timeImpl.setStart(start);
        timeImpl.setStop(stop);
        return timeImpl;
    }
    
    /** Returns an unbounded Time specification (i.e., "t=0 0").
     * @throws SdpException
     * @return an unbounded Time specification (i.e., "t=0 0").
     */    
    public Time createTime() {
        final TimeField timeImpl = new TimeField();
        timeImpl.setZero();
        return timeImpl;
    }
    
    /** Returns a RepeatTime object with the specified interval,
     *     duration, and time offsets.
     * @param repeatInterval the "repeat interval" in seconds
     * @param activeDuration the "active duration" in seconds
     * @param offsets  the list of offsets relative to the start time of
     *          the Time object with which the returned RepeatTime will be
     *          associated
     * @return RepeatTime
     */    
    public RepeatTime createRepeatTime(int repeatInterval,
                                   int activeDuration,
                                   int[] offsets) {
      RepeatField repeatTimeField=new RepeatField();                                 
       try {
           
            repeatTimeField.setRepeatInterval(repeatInterval);
            repeatTimeField.setActiveDuration(activeDuration);
            repeatTimeField.setOffsetArray(offsets);
           
        }
        catch(final SdpException e) {
            LOG.warn("Unexpected exception", e);
        } 
         return repeatTimeField;
    }
    
    /** Constructs a timezone adjustment record.
     * @param d the Date at which the adjustment is going to take
     *          place.
     * @param offset the adjustment in number of seconds relative to
     *          the start time of the SessionDescription with which this
     *          object is associated.
     * @return TimeZoneAdjustment
     */    
    public TimeZoneAdjustment createTimeZoneAdjustment(final Date d,
    int offset) {
         ZoneField timeZoneAdjustmentImpl=new ZoneField();
        try {
           
            Hashtable map = new Hashtable();
            map.put(d,new Integer(offset));
            timeZoneAdjustmentImpl.setZoneAdjustments(map);
        }
        catch(final SdpException e) {
            LOG.warn("Unexpected exception", e);
        }    
        return timeZoneAdjustmentImpl;
    }
    
    /** Returns a collection of Strings containing session description.
     * This method is no longer supported, as the semantics are unclear
     * and the original implementation caused a memory leak.
     * @param source String containing session descriptions.
     * @return a collection of Strings containing session descriptions.
     */    
    public static Vector findSessions(final String source) {
      // return sessionDescriptionsList;
      throw new UnsupportedOperationException ("Not implemented");
    }
    
    /**
     * @param ntpTime long to set
     * @return Returns a Date object for a given NTP date value.
     */    
    public static Date getDateFromNtp(long ntpTime) {
         return new Date((ntpTime - SdpConstants.NTP_CONST) * 1000);
    }
    
    /** Returns a long containing the NTP value for a given Java Date.
     * @param d Date to set
     * @return long
     */    
    public static long getNtpTime(Date d)  
        {
        if (d==null) return -1;
        return ((d.getTime()/1000) + SdpConstants.NTP_CONST);
        }
    }
