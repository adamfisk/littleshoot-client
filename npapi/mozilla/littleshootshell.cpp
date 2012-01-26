
#if XP_WIN
#include "windows.h"
#endif

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include <npapi.h>

#include "debug.hpp"

#include "little_shoot_plugin.hpp"
#include "npo_littleshoot.hpp"

#undef X11_RESIZE_DEBUG

#define WINDOW_TEXT "LittleShoot Plugin Loaded"

#if defined(__APPLE__)
// Unused
#elif defined(XP_WIN) // !__APPLE__
#define snprintf sprintf_s 
static LRESULT CALLBACK Manage(HWND hwnd, UINT msg, WPARAM wp, LPARAM lp);
#endif // __APPLE__

static NPP g_instance;

void StartLittleShoot()
{
    little_shoot_plugin * plugin = reinterpret_cast<little_shoot_plugin *>(
        g_instance->pdata
    );
    
    if (plugin)
    {
        plugin->start_process();
    }
}

void StopLittleShoot()
{
    little_shoot_plugin * plugin = reinterpret_cast<little_shoot_plugin *>(
        g_instance->pdata
    );
    
    if (plugin)
    {
        plugin->stop_process();
    }
}

char * NPP_GetMIMEDescription()
{
#if 1
    log_function();
#endif
    return PLUGIN_MIMETYPES;
}

NPError NPP_GetValue(NPP instance, NPPVariable variable, void * value )
{
#if 1
    log_function();
#endif
    
    static char psz_desc[1000];

    /* plugin class variables */
    switch (variable)
    {
        case NPPVpluginNameString:
            *((char **)value) = PLUGIN_NAME;
            return NPERR_NO_ERROR;

        case NPPVpluginDescriptionString:
            snprintf(psz_desc, sizeof(psz_desc), PLUGIN_DESCRIPTION, get_version());
            //log_debug("NPP_GetValue: psz_desc = " << static_cast<char *> (psz_desc));
            *((char **)value) = psz_desc;
            return NPERR_NO_ERROR;

        default:
            snprintf(psz_desc, sizeof(psz_desc), PLUGIN_DESCRIPTION,
                      get_version());
            /*log_debug(
                "NPP_GetValue: default = " << 
                reinterpret_cast<char *> (variable) << ":" << 
                reinterpret_cast<char *> (psz_desc)
            );*/
            ;
    }

    if (instance == 0)
    {
        return NPERR_INVALID_INSTANCE_ERROR;
    }

    little_shoot_plugin * plugin = reinterpret_cast<little_shoot_plugin *>(
        instance->pdata
    );
    
    if (0 == plugin)
    {
        return NPERR_INVALID_INSTANCE_ERROR;
    }

    switch (variable)
    {
        case NPPVpluginScriptableNPObject:
        {
            NPClass * scriptClass = plugin->getScriptClass();
            
            if (scriptClass)
            {
                *(NPObject **)value = NPN_CreateObject(instance, scriptClass);
                return NPERR_NO_ERROR;
            }
            break;
        }

        default:
            ;
    }
    return NPERR_GENERIC_ERROR;
}

NPError NPP_SetValue( NPP instance, NPNVariable variable, void *value )
{
#if 1
    log_function();
#endif
    
    return NPERR_GENERIC_ERROR;
}

#if defined(__APPLE__)
int16 NPP_HandleEvent( NPP instance, void * event )
{
    // We can intercept browser events here.
    return 0;
}
#endif /* __APPLE__ */

NPError NPP_Initialize()
{
#if 1
    log_function();
#endif
    return NPERR_NO_ERROR;
}

void NPP_Shutdown()
{
#if 1
    log_function();
#endif
}

NPError NPP_New(
    NPMIMEType pluginType, NPP instance, uint16 mode, int16 argc, 
    char * argn[], char * argv[], NPSavedData * saved
    )
{
    NPError status;
    
#if 1
    log_function();
#endif

    if (instance == 0)
    {
        log_debug("NPERR_INVALID_INSTANCE_ERROR");
        
        return NPERR_INVALID_INSTANCE_ERROR;
    }
    
    g_instance = instance;
    
    little_shoot_plugin * plugin = new little_shoot_plugin(instance, mode);
    
    if (0 == plugin)
    {
        log_debug("NPERR_OUT_OF_MEMORY_ERROR");
        
        return NPERR_OUT_OF_MEMORY_ERROR;
    }

    status = plugin->init(
        argc, 
        const_cast<const char **> (argn), 
        const_cast<const char **> (argv)
    );
    
    if (NPERR_NO_ERROR == status)
    {
        log_debug("NPERR_NO_ERROR");
        
        instance->pdata = reinterpret_cast<void *>(plugin);

        NPN_Status(instance, "LittleShoot P2P Plugin loaded.");
#if 0
        NPN_SetValue(instance, NPPVpluginWindowBool, (void *)false);
        NPN_SetValue(instance, NPPVpluginTransparentBool, (void *)false);
#endif
    }
    else
    {
        delete plugin;
    }
    return status;
}

NPError NPP_Destroy(NPP instance, NPSavedData ** save )
{
#if 1
    log_function();
#endif
    
    if (0 == instance)
    {
        return NPERR_INVALID_INSTANCE_ERROR;
    }

    little_shoot_plugin * plugin = reinterpret_cast<little_shoot_plugin *>(
        instance->pdata
    );
    
    if (0 == plugin)
    {
        return NPERR_NO_ERROR;
    }

    instance->pdata = 0;

#if XP_WIN
    HWND win = (HWND)plugin->get_window().window;
    
    WNDPROC winproc = plugin->get_wndproc();

    if (winproc)
    {
        SetWindowLong(win, GWL_WNDPROC, (LONG)winproc );
    }
#endif

    delete plugin;

    return NPERR_NO_ERROR;
}

NPError NPP_SetWindow(NPP instance, NPWindow * window)
{
#if 0
    log_function();
#endif
    return NPERR_NO_ERROR;
}

NPError NPP_NewStream(
    NPP instance, NPMIMEType type, NPStream * stream, 
    NPBool seekable, uint16 * stype
    )
{
    if (0 == instance)
    {
        return NPERR_INVALID_INSTANCE_ERROR;
    }

    little_shoot_plugin * plugin = reinterpret_cast<little_shoot_plugin *>(
        instance->pdata
    );
    
    if (0 == plugin)
    {
        return NPERR_INVALID_INSTANCE_ERROR;
    }
    
#if 1
	if (plugin->m_url.size())
	{
		log_debug(plugin->m_url);
	}
#endif

    if (stream->url && stream->headers)
    {
#if 0
        log_debug("New Stream url(" << stream->url << "), end(" << stream->end << "), lastmodified(" << stream->lastmodified << "), headers(" << stream->headers << ").");
#endif

        littleshoot::stream_entry e(
            stream->url, stream->end, stream->lastmodified, stream->headers
        );
        
        boost::shared_ptr<littleshoot::stream> s(new littleshoot::stream(e));
        
        plugin->insert_stream(s);
    }

    if (!plugin->m_url.size() || strcmp(stream->url, plugin->m_url.c_str()))
    {
		*stype = NP_NORMAL;
        return NPERR_NO_ERROR;
    }
    return NPERR_GENERIC_ERROR;
}

/**
 * Determines maximum number of bytes that the plug-in can consume.
 * @param instance Pointer to the current plug-in instance.
 * @param stream Pointer to the current stream.
 */
int32 NPP_WriteReady(NPP instance, NPStream * stream)
{
#if 0
    log_function();
#endif
    return 1492;
}

/**
 * Delivers data to a plug-in instance.
 * @param instance Pointer to the current plug-in instance.
 * @param stream Pointer to the current stream.
 * @param offset Offset in bytes of buf from the beginning of the data in the 
 * stream. Can be used to check stream progress or bye range requests from 
 * NPN_RequestRead.
 * @paramlen Length in bytes of buf; number of bytes accepted.
 * @parambuf Buffer of data, delivered by the stream, that contains len bytes 
 * of data offset bytes from the start of the stream. The buffer is allocated 
 * by the browser and is deleted after returning from the function, so the 
 * plug-in should make a copy of the data it needs to keep.
 */
int32 NPP_Write(NPP instance, NPStream * stream, int32 offset, int32 len, void * buffer)
{
#if 1
    log_debug("Stream offset(" << offset << "), len(" << len << ").");
#endif

    little_shoot_plugin * plugin = reinterpret_cast<little_shoot_plugin *>(
        instance->pdata
    );
    
    if (!plugin)
    {
        return NPERR_INVALID_INSTANCE_ERROR;
    }
    else
    {
        char * buf = reinterpret_cast<char *> (buffer);
        
        const char * url = stream->url;

        plugin->write_stream(url, offset, len, buf);
    }
    
    return len;
}

NPError NPP_DestroyStream(NPP instance, NPStream * stream, NPError reason)
{
#if 1
    log_function();
#endif
    if (!instance)
    {
        return NPERR_INVALID_INSTANCE_ERROR;
    }
    else
    {
        little_shoot_plugin * plugin = reinterpret_cast<little_shoot_plugin *>(
            instance->pdata
        );
    
        if (!plugin)
        {
            return NPERR_INVALID_INSTANCE_ERROR;
        }
        else
        {
            plugin->destroy_stream(stream->url);
            
            if (!plugin->get_stream())
            {
                const char * download_test_url = 
                    "http://www.littleshoot.org/downloadsWindow";

                NPN_GetURL(
                    instance, download_test_url, "_self"
                );
                
                /*
                static bool did_open_download_window = false;
                
                if (!did_open_download_window)
                {
                    did_open_download_window = true;
                    
                    NPN_GetURL(
                        instance, download_test_url, "_blank"
                    );
                }
            
                NPN_GetURL(
                    instance, "javascript:history.go(-1)", "_self"
                );
                 */
//            
//            
//                NPStream * s = plugin->get_stream();
//            
//            NPN_NewStream(instance, "text/html", 0, &s);
//            
//            
//            char html_buf[] = "<html>\n<body>\n\n<h2 align=center>LittleShoot is loading foo.bar...</h2>\n\n</body>\n</html>";
//            
//            int32 written = NPN_Write(instance, s, strlen(html_buf), html_buf);
//                
//                log_debug("Wrote " << written << " bytes.");
            
            }
            
           // NPN_DestroyStream(instance, s, NPRES_DONE);
        }
    }
    return NPERR_NO_ERROR;
}

void NPP_StreamAsFile(NPP instance, NPStream * stream, const char * fname )
{
#if 1
    log_function();
#endif
    if (instance == 0 )
    {
        return;
    }
    
    // We can stream data from the browser here.
    if (stream && fname)
    {
        log_debug("stream = " << stream << ", fname = " << fname);
    }
    
    if (fname)
    {

    }
}
 
void NPP_URLNotify(
    NPP instance, const char * url, NPReason reason, void * notifyData
    )
{
    // We can handle URL's here.
#if 1
    log_function();
#endif
}

void NPP_Print(NPP instance, NPPrint * printInfo)
{
#if 1
    log_function();
#endif

    if (printInfo == 0 )
    {
        return;
    }

    if (instance != 0 )
    {
        if (printInfo->mode == NP_FULL)
        {
            printInfo->print.fullPrint.pluginPrinted = false;
        }
        else
        {

        }
    }
}

#if defined(XP_WIN)
static LRESULT CALLBACK Manage(HWND hwnd, UINT msg, WPARAM wp, LPARAM lp)
{
    little_shoot_plugin * p = reinterpret_cast<little_shoot_plugin *>(
        GetWindowLongPtr(hwnd, GWLP_USERDATA)
    );
    
    switch (msg)
    {
        case WM_ERASEBKGND:
            return 1L;
        case WM_PAINT:
        {
            PAINTSTRUCT paintstruct;
            HDC hdc;
            RECT rect;

            hdc = BeginPaint(hwnd, &paintstruct);

            GetClientRect(hwnd, &rect);

            FillRect(hdc, &rect, (HBRUSH)GetStockObject(WHITE_BRUSH));
            
            SetTextColor(hdc, RGB(0, 0, 0));
            SetBkColor(hdc, RGB(255, 255, 255));
            
            DrawText(
                hdc, WINDOW_TEXT, strlen(WINDOW_TEXT), 
                &rect, DT_CENTER | DT_VCENTER | DT_SINGLELINE
            );

            EndPaint(hwnd, &paintstruct);
            return 0L;
        }
        default:
        if (p->get_wndproc())
        {
            return CallWindowProc(p->get_wndproc(), hwnd, msg, wp, lp);
        }
    }
    
    return 0L;
}
#endif /* XP_WIN */

