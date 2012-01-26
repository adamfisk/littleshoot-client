
#include <cstdio>
#include <ctype.h>

#if XP_WIN
#include "Windows.h"
#define strcasecmp stricmp
#endif

#include "debug.hpp"

#include "npo_littleshoot.hpp"
#include "little_shoot_plugin.hpp"

#include <path.hpp>

using namespace littleshoot;

little_shoot_plugin::little_shoot_plugin(NPP instance, uint16 mode)
	: i_npmode(mode)
    , m_url("")
    , m_script_class(0)
    , m_browser(instance)
    , m_base_url(0)
    , m_stream(0)
#if XP_WIN
    , m_wndproc(0)
#endif
{
	std::memset(&npwindow, 0, sizeof(NPWindow));
}

NPError little_shoot_plugin::init(
    int argc, const char * argn[], const char * argv[]
    )
{
    // Create any runtime files and/or directories.
    path::instance().create_runtime_paths_if();
    
    littleshoot::logger::instance().set_output_file(
        "littleshoot.npapi.plugin.log"
    );
    
    const char * ppsz_argv[32];
    int ppsz_argc = 0;

    m_script_class = np_runtime_class<little_shoot_root_np_object>::getClass();

    ppsz_argv[ppsz_argc++] = "-verbose";

    for (unsigned int i = 0; i < argc ; i++)
    {
        log_debug(
            "Init arguments, argn(" << argn[i] << "), argv(" << argv[i] << ")."
        )
    }

#if 0
    NPObject * plugin;
    
    NPN_GetValue(m_browser, NPNVWindowNPObject, &plugin);

    NPIdentifier identifier = NPN_GetStringIdentifier("location");

    NPVariant variantValue;

    NPN_GetProperty(m_browser, plugin, identifier, &variantValue);

    NPObject * locationObj = variantValue.value.objectValue;

    identifier = NPN_GetStringIdentifier("href");

    NPN_GetProperty(m_browser, locationObj, identifier, &variantValue);

    NPString npStr = NPVARIANT_TO_STRING(variantValue);
    
    for (unsigned int i = 0; i < npStr.utf8length; i++)
    {
        m_url += npStr.utf8characters[i];
    }

    NPN_ReleaseObject(locationObj);
#endif

    if (m_url.size())
    {
        char * absolute_url = get_url(m_url.c_str());
        m_url = absolute_url ? absolute_url : strdup(m_url.c_str());
        
        log_debug("Absolute URL(" << absolute_url << ").");
        log_debug("Raw URL(" << m_url << ").");
    }
    
    little_shoot_ipc_.reset(new little_shoot_ipc(argc, argv));
    
    if (!little_shoot_ipc_)
    {
        return NPERR_GENERIC_ERROR;
    }

    little_shoot_ipc_->start();

    return NPERR_NO_ERROR;
}

little_shoot_plugin::~little_shoot_plugin()
{
    log_function();
    
    // :FIXME: These do not need to be heap alocated.
    delete m_base_url;
    
    // Do not stop LittleShoot on deallocation.
    if (false)
    {
        little_shoot_ipc_->stop();
    }
}

bool little_shoot_plugin::is_authenticated() const
{
    return 
        m_url.find("littleshoot.") != std::string::npos  ||
#ifndef XP_WIN
        m_url.find("file:///") != std::string::npos
#else
        m_url.find("C:\\") != std::string::npos
#endif
    ;
}

char * little_shoot_plugin::get_url(const char * url)
{
#if 1
    log_function();
#endif
     
     log_debug(url);
     
    if (0 != url)
    {
        // check whether URL is already absolute
        const char * end = strchr(url, ':');
        
        if ((0 != end) && (end != url) )
        {
            // validate protocol header
            const char *start = url;
            char c = *start;
            if (isalpha(c) )
            {
                ++start;
                while ( start != end )
                {
                    c  = *start;
                    if (! (isalnum(c)
                       || ('-' == c)
                       || ('+' == c)
                       || ('.' == c)
                       || ('/' == c)))
                        // not valid protocol header, assume relative URL
                        goto relativeurl;
                    ++start;
                }
                /* we have a protocol header, therefore URL is absolute */
                return strdup(url);
            }
            // not a valid protocol header, assume relative URL
        }

relativeurl:

        if  (m_base_url)
        {
            std::size_t baseLen = strlen(m_base_url);
            char  * href = new char[baseLen + strlen(url) + 1];
            if (href )
            {
                /* prepend base URL */
                strcpy(href, m_base_url);

                /*
                ** relative url could be empty,
                ** in which case return base URL
                */
                if ('\0' == *url )
                {
                    return href;
                }

                /*
                ** locate pathname part of base URL
                */

                /* skip over protocol part  */
                char * pathstart = strchr(href, ':');
                
                char * pathend;
                
                if (pathstart)
                {
                    if ('/' == *(++pathstart))
                    {
                        if ('/' == *(++pathstart))
                        {
                            ++pathstart;
                        }
                    }
                    /* skip over host part */
                    pathstart = strchr(pathstart, '/');
                    
                    pathend = href+baseLen;
                    
                    if (!pathstart)
                    {
                        // no path, add a / past end of url (over '\0')
                        pathstart = pathend;
                        *pathstart = '/';
                    }
                }
                else
                {
                    /* baseURL is just a UNIX path */
                    if ('/' != *href)
                    {
                        /* baseURL is not an absolute path */
                        delete href;
                        return 0;
                    }
                    pathstart = href;
                    pathend = href + baseLen;
                }

                /* relative URL made of an absolute path ? */
                if ('/' == *url)
                {
                    /* replace path completely */
                    strcpy(pathstart, url);
                    return href;
                }

                /* find last path component and replace it */
                while ( '/' != *pathend)
                {
                    --pathend;
                }

                /*
                ** if relative url path starts with one or more '../',
                ** factor them out of href so that we return a
                ** normalized URL
                */
                while (pathend != pathstart)
                {
                    const char * p = url;
                    
                    if ('.' != *p)
                    {
                        break;
                    }
                    
                    ++p;
                    
                    if ('\0' == *p)
                    {
                        /* relative url is just '.' */
                        url = p;
                        break;
                    }
                    
                    if ('/' == *p)
                    {
                        /* relative url starts with './' */
                        url = ++p;
                        continue;
                    }
                    
                    if ('.' != *p)
                    {
                        break;
                    }
                    
                    ++p;
                    
                    if ('\0' == *p)
                    {
                        /* relative url is '..' */
                    }
                    else
                    {
                        if ('/' != *p)
                        {
                            break;
                        }
                        /* relative url starts with '../' */
                        ++p;
                    }
                    
                    url = p;
                    
                    do
                    {
                        --pathend;
                    }
                    while( '/' != *pathend);
                }
                /* skip over '/' separator */
                ++pathend;
                /* concatenate remaining base URL and relative URL */
                strcpy(pathend, url);
            }
            return href;
        }
    }
    return 0;
}

