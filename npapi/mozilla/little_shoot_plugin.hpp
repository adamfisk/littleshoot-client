
#ifndef LITTLE_SHOOT_PLUGIN_HPP
#define LITTLE_SHOOT_PLUGIN_HPP

#include <map>

#include <boost/shared_ptr.hpp>

#include "npapi.h"
#include "npruntime.h"

#include "little_shoot_ipc.hpp"
#include "debug.hpp"

#include <stream.hpp>
#include <stream_entry.hpp>

#if !defined(__APPLE__) && !defined(XP_UNIX) && !defined(XP_WIN)
#define XP_UNIX 1
#elif defined(__APPLE__)
#undef XP_UNIX
#endif

#if defined(__APPLE__)
#include <CoreServices/CoreServices.h>
#endif

#ifndef __MAX
#define __MAX(a, b) (((a) > (b)) ? (a) : (b))
#endif
#ifndef __MIN
#define __MIN(a, b) (((a) < (b)) ? (a) : (b))
#endif

using namespace littleshoot;

class little_shoot_plugin
{
    public:
    
        little_shoot_plugin(NPP, uint16);
        
        virtual ~little_shoot_plugin();

        NPError init(int argc, const char * argn[], const char * argv[]);
        
        void start_process()
        {
            little_shoot_ipc_->start();
        }
        
        void stop_process()
        {
            little_shoot_ipc_->stop();
        }
        
        little_shoot_ipc_t * get_ipc()
        {
            return little_shoot_ipc_.get();
        }
    
        NPP get_browser()
        {
            return m_browser;
        };
        
        /**
         * Determines if the remote domain is authenticated and can make use of
         * private plugin API's.
         * @return true If the remote domain is authenticated.
         */
        bool is_authenticated() const;
        
        char * get_url(const char * url);
        
        NPStream * get_stream()
        {
            return m_stream;
        }
        
        NPWindow & get_window()
        {
            return npwindow;
        }
        
        void setWindow(const NPWindow & window)
        {
            npwindow = window; 
        };

        NPClass * getScriptClass()
        {
            return m_script_class;
        }

        uint16 i_npmode; /* either NP_EMBED or NP_FULL */

        /**
         * The url object assigned in "src = " html.
         */
        std::string m_url;
        
#if XP_WIN
         WNDPROC get_wndproc()
         {
             return m_wndproc;
         }
#endif
        /**
         * Adds a managed stream.
         */
        void insert_stream(boost::shared_ptr<littleshoot::stream> & s)
        {
            streams_.insert(
                std::make_pair<std::string, boost::shared_ptr<littleshoot::stream> > (s->entry().url(), s)
            );
        }
        
        bool write_stream(const char * url, boost::int32_t offset, boost::int32_t len, char * buf)
        {
            bool ret = false;
            
            std::map<std::string, boost::shared_ptr<littleshoot::stream> >::iterator 
                it = streams_.find(url);
                
            if (it != streams_.end())
            {
#if 0
                log_debug("Found existing stream url(" << it->second->entry().url() << ").");
#endif
                it->second->write(buf, len, offset, true);
                
                ret = true;
            }
            
            if (!ret)
            {
                log_debug(
                    "ERROR: No existing stream for url(" << 
                    (*it).second->entry().url().c_str() << ")."
                );
            }   
            
            //assert(ret == true);
            
            return ret;
        }
        
        void destroy_stream(const char * url)
        {
            std::map<std::string, boost::shared_ptr<littleshoot::stream> >::iterator 
                it = streams_.find(url);
                
            if (it != streams_.end())
            {
                //log_debug("Destroying stream stream url(%s)", it->second->entry().url().c_str());
                
                it->second->deallocate();
                streams_.erase(it);
            }
        }

    private:
    
        boost::shared_ptr<little_shoot_ipc_t> little_shoot_ipc_;
        
        /**
         * This vector holds stream encapsulations that will be passed to the
         * littleshoot process via IPC.
         */
        std::map<std::string, boost::shared_ptr<littleshoot::stream> > streams_;
    
        NPClass * m_script_class;

        NPP m_browser;
        
        char * m_base_url;
        
        NPStream * m_stream;
        
        NPWindow npwindow;
        
#if XP_WIN
    WNDPROC m_wndproc;
#endif

};

#define PLUGIN_NAME  "LittleShoot PlugIn"
#define PLUGIN_DESCRIPTION \
    "Version %s, Copyright 2008 LastBamboo LLC." \
    "<br><a href=\"http://www.littleshoot.org/\">" \
    "http://www.littleshoot.org/</a>"

#define PLUGIN_MIMETYPES \
    /* LittleShoot */ \
    "application/x-littleshoot-plugin:littleshoot:LittleShoot plug-in;" \
    "application/x-littleshoot:littleshoot:LittleShoot plug-in;" \
    "application/x-bittorrent::torrent::Torrent Torrent;" \
    /* MPEG-1 and MPEG-2 */ \
    "audio/mpeg:mp2,mp3,mpga,mpega:MPEG audio;" \
    "audio/x-mpeg:mp2,mp3,mpga,mpega:MPEG audio;" \
    "video/mpeg:mpg,mpeg,mpe:MPEG video;" \
    "video/x-mpeg:mpg,mpeg,mpe:MPEG video;" \
    "video/mpeg-system:mpg,mpeg,mpe,vob:MPEG video;" \
    "video/x-mpeg-system:mpg,mpeg,mpe,vob:MPEG video;" \
    /* M3U */ \
    "audio/x-mpegurl:m3u:MPEG audio;" \
    /* MPEG-4 */ \
    "video/mp4:mp4,mpg4:MPEG-4 video;" \
    "audio/mp4:mp4,mpg4:MPEG-4 audio;" \
    "audio/x-m4a:m4a:MPEG-4 audio;" \
    "application/mpeg4-iod:mp4,mpg4:MPEG-4 video;" \
    "application/mpeg4-muxcodetable:mp4,mpg4:MPEG-4 video;" \
    /* AVI */ \
    "video/x-msvideo:avi:AVI video;" \
    /* QuickTime */ \
    "video/quicktime:mov,qt:QuickTime video;" \
    /* OGG */ \
    "application/x-ogg:ogg:Ogg stream;" \
    "application/ogg:ogg:Ogg stream;" \
    /* Windows Media */ \
    "video/x-ms-asf-plugin:asf,asx:Windows Media Video;" \
    "video/x-ms-asf:asf,asx:Windows Media Video;" \
    "application/x-mplayer2::Windows Media;" \
    "video/x-ms-wmv:wmv:Windows Media;" \
    "video/x-ms-wvx:wvx:Windows Media Video;" \
    "audio/x-ms-wma:wma:Windows Media Audio;" \
    /* Google LittleShoot */ \
    "application/x-google-littleshoot-plugin::Google LittleShoot plug-in;" \
    /* WAV audio */ \
    "audio/wav:wav:WAV audio;" \
    "audio/x-wav:wav:WAV audio;" \
    /* 3GPP */ \
    "audio/3gpp:3gp,3gpp:3GPP audio;" \
    "video/3gpp:3gp,3gpp:3GPP video;" \
    /* 3GPP2 */ \
    "audio/3gpp2:3g2,3gpp2:3GPP2 audio;" \
    "video/3gpp2:3g2,3gpp2:3GPP2 video;" \
    /* DIVX */ \
    "video/divx:divx:DivX video;" \
    /* FLV */ \
    "video/flv:flv:FLV video;" \
    "video/x-flv:flv:FLV video;" \
    /* Matroska */ \
    "video/x-matroska:mkv:Matroska video;" \
    "audio/x-matroska:mka:Matroska audio;" \
    /* XSPF */ \
    "application/xspf+xml:xspf:Playlist xspf;"

#endif // 
