
#include <string.h>

#include <CoreServices/CoreServices.h>

#include "debug.hpp"
#include "little_shoot_plugin.hpp"

#undef TARGET_RT_MAC_CFM

#include <sys/mman.h>

#include "npapi.h"

#ifdef XP_UNIX
#undef XP_UNIX
#endif

#include "npupp.h"

#if defined(__APPLE__) && defined(__POWERPC__) && (!defined(TARGET_RT_MAC_CFM))

using namespace littleshoot;

// glue for mapping outgoing Macho function pointers to TVectors
struct TFPtoTVGlue
{
    void * glue[2];
};

static struct
{
    TFPtoTVGlue     newp;
    TFPtoTVGlue     destroy;
    TFPtoTVGlue     setwindow;
    TFPtoTVGlue     newstream;
    TFPtoTVGlue     destroystream;
    TFPtoTVGlue     asfile;
    TFPtoTVGlue     writeready;
    TFPtoTVGlue     write;
    TFPtoTVGlue     print;
    TFPtoTVGlue     event;
    TFPtoTVGlue     urlnotify;
    TFPtoTVGlue     getvalue;
    TFPtoTVGlue     setvalue;

    TFPtoTVGlue     shutdown;
} gPluginFuncsGlueTable;

static inline void * SetupFPtoTVGlue(TFPtoTVGlue * functionGlue, void * fp)
{
    functionGlue->glue[0] = fp;
    functionGlue->glue[1] = 0;
    return functionGlue;
}

#define PLUGIN_TO_HOST_GLUE(name, fp) (SetupFPtoTVGlue( \
    &gPluginFuncsGlueTable.name, (void*)fp))

// glue for mapping netscape TVectors to Macho function pointers
struct TTVtoFPGlue
{
    uint32 glue[6];
};

static struct
{
    TTVtoFPGlue geturl;
    TTVtoFPGlue posturl;
    TTVtoFPGlue requestread;
    TTVtoFPGlue newstream;
    TTVtoFPGlue write;
    TTVtoFPGlue destroystream;
    TTVtoFPGlue status;
    TTVtoFPGlue uagent;
    TTVtoFPGlue memalloc;
    TTVtoFPGlue memfree;
    TTVtoFPGlue memflush;
    TTVtoFPGlue reloadplugins;
    TTVtoFPGlue getJavaEnv;
    TTVtoFPGlue getJavaPeer;
    TTVtoFPGlue geturlnotify;
    TTVtoFPGlue posturlnotify;
    TTVtoFPGlue getvalue;
    TTVtoFPGlue setvalue;
    TTVtoFPGlue invalidaterect;
    TTVtoFPGlue invalidateregion;
    TTVtoFPGlue forceredraw;
    // NPRuntime support
    TTVtoFPGlue getstringidentifier;
    TTVtoFPGlue getstringidentifiers;
    TTVtoFPGlue getintidentifier;
    TTVtoFPGlue identifierisstring;
    TTVtoFPGlue utf8fromidentifier;
    TTVtoFPGlue intfromidentifier;
    TTVtoFPGlue createobject;
    TTVtoFPGlue retainobject;
    TTVtoFPGlue releaseobject;
    TTVtoFPGlue invoke;
    TTVtoFPGlue invokeDefault;
    TTVtoFPGlue evaluate;
    TTVtoFPGlue getproperty;
    TTVtoFPGlue setproperty;
    TTVtoFPGlue removeproperty;
    TTVtoFPGlue hasproperty;
    TTVtoFPGlue hasmethod;
    TTVtoFPGlue releasevariantvalue;
    TTVtoFPGlue setexception;
} g_ns_function_procsGlueTable;

static void * SetupTVtoFPGlue(TTVtoFPGlue * functionGlue, void * tvp)
{
    static const TTVtoFPGlue glueTemplate = 
    {
        0x3D800000, 0x618C0000, 0x800C0000, 0x804C0004, 0x7C0903A6, 0x4E800420
    };

    std::memcpy(functionGlue, &glueTemplate, sizeof(TTVtoFPGlue));
    
    functionGlue->glue[0] |= ((UInt64)tvp >> 16);
    functionGlue->glue[1] |= ((UInt64)tvp & 0xFFFF);
#if 1
    mprotect(functionGlue, sizeof(TTVtoFPGlue), PROT_READ);
#else // 32 bit only Carbon function.
    ::MakeDataExecutable(functionGlue, sizeof(TTVtoFPGlue));
#endif
    
    return functionGlue;
}

#define HOST_TO_PLUGIN_GLUE(name, fp) (SetupTVtoFPGlue(&g_ns_function_procsGlueTable.name, (void*)fp))

#else

#define PLUGIN_TO_HOST_GLUE(name, fp) (fp)
#define HOST_TO_PLUGIN_GLUE(name, fp) (fp)

#endif /* __APPLE__ */


#pragma mark -


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//
// Globals
//
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

#if !TARGET_API_MAC_CARBON
QDGlobals*      gQDPtr; // Pointer to Netscape’s QuickDraw globals
#endif
short           gResFile;           // Refnum of the plugin’s resource file
NPNetscapeFuncs    g_ns_function_procs;      // Function table for procs in Netscape called by plugin

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//
// Wrapper functions for all calls from the plugin to Netscape.
// These functions let the plugin developer just call the APIs
// as documented and defined in npapi.h, without needing to know
// about the function table and call macros in npupp.h.
//
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
void NPN_Version(int * plugin_major, int * plugin_minor, int * netscape_major, int * netscape_minor)
{
#if 1
    log_function();
#endif
    *plugin_major = NP_VERSION_MAJOR;
    *plugin_minor = NP_VERSION_MINOR;
    *netscape_major = g_ns_function_procs.version >> 8;
    *netscape_minor = g_ns_function_procs.version & 0xFF;
}

NPError NPN_GetURLNotify(NPP instance, const char * url, const char * window, void * notifyData)
{
#if 1
    log_function();
#endif
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    NPError err;

    if (version_minor >= NPVERS_HAS_NOTIFICATION)
    {
        err = CallNPN_GetURLNotifyProc(
            g_ns_function_procs.geturlnotify, instance, url, window, notifyData
        );
    }
    else
    {
        err = NPERR_INCOMPATIBLE_VERSION_ERROR;
    }
    return err;
}

NPError NPN_GetURL(NPP instance, const char* url, const char* window)
{
#if 1
    log_function();
#endif
    return CallNPN_GetURLProc(g_ns_function_procs.geturl, instance, url, window);
}

NPError NPN_PostURLNotify(NPP instance, const char* url, const char* window, uint32 len, const char* buf, NPBool file, void* notifyData)
{
#if 1
    log_function();
#endif
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    NPError err;

    if (version_minor >= NPVERS_HAS_NOTIFICATION )
    {
        err = CallNPN_PostURLNotifyProc(g_ns_function_procs.posturlnotify, instance, url,
        window, len, buf, file, notifyData);
    }
    else
    {
        err = NPERR_INCOMPATIBLE_VERSION_ERROR;
    }
    return err;
}

NPError NPN_PostURL(NPP instance, const char* url, const char* window, uint32 len, const char* buf, NPBool file)
{
#if 1
    log_function();
#endif
    return CallNPN_PostURLProc(g_ns_function_procs.posturl, instance, url, window, len, buf, file);
}

NPError NPN_RequestRead(NPStream* stream, NPByteRange* rangeList)
{
#if 1
    log_function();
#endif
    return CallNPN_RequestReadProc(g_ns_function_procs.requestread, stream, rangeList);
}

NPError NPN_NewStream(NPP instance, NPMIMEType type, const char* window, NPStream** stream)
{
#if 1
    log_function();
#endif
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    NPError err;

    if (version_minor >= NPVERS_HAS_STREAMOUTPUT )
    {
        err = CallNPN_NewStreamProc(g_ns_function_procs.newstream, instance, type, window, stream);
    }
    else
    {
        err = NPERR_INCOMPATIBLE_VERSION_ERROR;
    }
    return err;
}

int32 NPN_Write(NPP instance, NPStream * stream, int32 len, void * buffer)
{
#if 1
    log_function();
#endif

    int version_minor = g_ns_function_procs.version & 0xFF;
    
    NPError err;

    if (version_minor >= NPVERS_HAS_STREAMOUTPUT )
    {
        err = CallNPN_WriteProc(g_ns_function_procs.write, instance, stream, len, buffer);
    }
    else
    {
        err = NPERR_INCOMPATIBLE_VERSION_ERROR;
    }
    return err;
}

NPError NPN_DestroyStream(NPP instance, NPStream* stream, NPError reason)
{
#if 1
    log_function();
#endif

    int version_minor = g_ns_function_procs.version & 0xFF;
    
    NPError err;

    if (version_minor >= NPVERS_HAS_STREAMOUTPUT)
    {
        err = CallNPN_DestroyStreamProc(
            g_ns_function_procs.destroystream, instance, stream, reason
        );
    }
    else
    {
        err = NPERR_INCOMPATIBLE_VERSION_ERROR;
    }
    return err;
}

void NPN_Status(NPP instance, const char* message)
{
#if 1
    log_function();
#endif
    CallNPN_StatusProc(g_ns_function_procs.status, instance, message);
}

const char* NPN_UserAgent(NPP instance)
{
#if 1
    log_function();
#endif
    return CallNPN_UserAgentProc(g_ns_function_procs.uagent, instance);
}

void * NPN_MemAlloc(uint32 size)
{
    return CallNPN_MemAllocProc(g_ns_function_procs.memalloc, size);
}

void NPN_MemFree(void * ptr)
{
    CallNPN_MemFreeProc(g_ns_function_procs.memfree, ptr);
}

uint32 NPN_MemFlush(uint32 size)
{
    return CallNPN_MemFlushProc(g_ns_function_procs.memflush, size);
}

void NPN_ReloadPlugins(NPBool reloadPages)
{
#if 1
    log_function();
#endif
    CallNPN_ReloadPluginsProc(g_ns_function_procs.reloadplugins, reloadPages);
}

#ifdef OJI
JRIEnv * NPN_GetJavaEnv()
{
    return CallNPN_GetJavaEnvProc( g_ns_function_procs.getJavaEnv );
}

jobject NPN_GetJavaPeer(NPP instance)
{
    return CallNPN_GetJavaPeerProc( g_ns_function_procs.getJavaPeer, instance );
}
#endif

NPError NPN_GetValue(NPP instance, NPNVariable variable, void *value)
{
    return CallNPN_GetValueProc( g_ns_function_procs.getvalue, instance, variable, value);
}

NPError NPN_SetValue(NPP instance, NPPVariable variable, void *value)
{
    return CallNPN_SetValueProc( g_ns_function_procs.setvalue, instance, variable, value);
}

void NPN_InvalidateRect(NPP instance, NPRect *rect)
{
    CallNPN_InvalidateRectProc( g_ns_function_procs.invalidaterect, instance, rect);
}

void NPN_InvalidateRegion(NPP instance, NPRegion region)
{
    CallNPN_InvalidateRegionProc( g_ns_function_procs.invalidateregion, instance, region);
}

void NPN_ForceRedraw(NPP instance)
{
    CallNPN_ForceRedrawProc( g_ns_function_procs.forceredraw, instance);
}

NPIdentifier NPN_GetStringIdentifier(const NPUTF8 * name)
{
#if 1
    log_function();
#endif

    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_GetStringIdentifierProc(g_ns_function_procs.getstringidentifier, name);
    }
    return 0;
}

void NPN_GetStringIdentifiers(const NPUTF8 **names, int32_t nameCount, NPIdentifier *identifiers)
{
#if 1
    log_function();
#endif

    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        CallNPN_GetStringIdentifiersProc(g_ns_function_procs.getstringidentifiers, names, nameCount, identifiers);
    }
}

NPIdentifier NPN_GetIntIdentifier(int32_t intid)
{
#if 1
    log_function();
#endif
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_GetIntIdentifierProc(g_ns_function_procs.getintidentifier, intid);
    }
    return 0;
}

bool NPN_IdentifierIsString(NPIdentifier identifier)
{
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_IdentifierIsStringProc(g_ns_function_procs.identifierisstring, identifier);
    }
    return false;
}

NPUTF8 * NPN_UTF8FromIdentifier(NPIdentifier identifier)
{
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_UTF8FromIdentifierProc(g_ns_function_procs.utf8fromidentifier, identifier);
    }
    return 0;
}

int32_t NPN_IntFromIdentifier(NPIdentifier identifier)
{
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_IntFromIdentifierProc(
            g_ns_function_procs.intfromidentifier, identifier
        );
    }
    return 0;
}

NPObject * NPN_CreateObject(NPP instance, NPClass *np_class)
{
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_CreateObjectProc(
            g_ns_function_procs.createobject, instance, np_class
        );
    }
    return 0;
}

NPObject * NPN_RetainObject(NPObject *npobj)
{
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_RetainObjectProc(
            g_ns_function_procs.retainobject, npobj
        );
    }
    return 0;
}

void NPN_ReleaseObject(NPObject * npobj)
{
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        CallNPN_ReleaseObjectProc(g_ns_function_procs.releaseobject, npobj);
    }
}

bool NPN_Invoke(
    NPP instance, NPObject * npobj, NPIdentifier methodName, 
    const NPVariant * args, uint32_t argc, NPVariant * result
    )
{
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_InvokeProc(
            g_ns_function_procs.invoke, instance, npobj, 
            methodName, args, argc, result
        );
    }
    return false;
}

bool NPN_InvokeDefault(
    NPP instance, NPObject * npobj, const NPVariant *args, 
    uint32_t argc, NPVariant * result
    )
{
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_InvokeDefaultProc(
            g_ns_function_procs.invokeDefault, instance, npobj, 
            args, argc, result
        );
    }
    return false;
}

bool NPN_Evaluate(
    NPP instance, NPObject * npobj, NPString * script, NPVariant * result
    )
{
#if 1
    log_function();
#endif
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_EvaluateProc(
            g_ns_function_procs.evaluate, instance, npobj, script, result
        );
    }
    return false;
}

bool NPN_GetProperty(
    NPP instance, NPObject * npobj, NPIdentifier propertyName, NPVariant * result
    )
{
#if 1
    log_function();
#endif
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14)
    {
        return CallNPN_GetPropertyProc(
            g_ns_function_procs.getproperty, instance, npobj, 
            propertyName, result
        );
    }
    return false;
}

bool NPN_SetProperty(
    NPP instance, NPObject * npobj, NPIdentifier propertyName, 
    const NPVariant * value
    )
{
#if 1
    log_function();
#endif
    int version_minor = g_ns_function_procs.version & 0xFF;
    if (version_minor >= 14 )
    {
        return CallNPN_SetPropertyProc(
            g_ns_function_procs.setproperty, instance, npobj, propertyName, 
            value
        );
    }
    return false;
}

bool NPN_RemoveProperty(
    NPP instance, NPObject  *npobj, NPIdentifier propertyName
    )
{
#if 1
    log_function();
#endif
    int version_minor = g_ns_function_procs.version & 0xFF;
    if (version_minor >= 14 )
    {
        return CallNPN_RemovePropertyProc(
            g_ns_function_procs.removeproperty, instance, npobj, propertyName
        );
    }
    return false;
}

bool NPN_HasProperty(NPP instance, NPObject *npobj, NPIdentifier propertyName)
{
#if 1
    log_function();
#endif
    int version_minor = g_ns_function_procs.version & 0xFF;
    if (version_minor >= 14 )
    {
        return CallNPN_HasPropertyProc(
            g_ns_function_procs.hasproperty, instance, npobj, propertyName
        );
    }
    return false;
}

bool NPN_HasMethod(NPP instance, NPObject *npobj, NPIdentifier methodName)
{
#if 1
    log_function();
#endif
    int version_minor = g_ns_function_procs.version & 0xFF;
    if (version_minor >= 14 )
    {
        return CallNPN_HasMethodProc(
            g_ns_function_procs.hasmethod, instance, npobj, methodName
        );
    }
    return false;
}

void NPN_ReleaseVariantValue(NPVariant *variant)
{
    int version_minor = g_ns_function_procs.version & 0xFF;
    
    if (version_minor >= 14 )
    {
        CallNPN_ReleaseVariantValueProc(
            g_ns_function_procs.releasevariantvalue, variant
        );
    }
}

void NPN_SetException(NPObject *npobj, const NPUTF8 *message)
{
    int version_minor = g_ns_function_procs.version & 0xFF;
    if (version_minor >= 14 )
    {
        CallNPN_SetExceptionProc(
            g_ns_function_procs.setexception, npobj, message
        );
    }
}

#pragma mark -

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//
// Wrapper functions for all calls from Netscape to the plugin.
// These functions let the plugin developer just create the APIs
// as documented and defined in npapi.h, without needing to
// install those functions in the function table or worry about
// setting up globals for 68K plugins.
//
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

NPError     Private_Initialize();
void        Private_Shutdown();
NPError     Private_New(NPMIMEType pluginType, NPP instance, uint16 mode, int16 argc, char* argn[], char* argv[], NPSavedData* saved);
NPError     Private_Destroy(NPP instance, NPSavedData** save);
NPError     Private_SetWindow(NPP instance, NPWindow* window);
NPError     Private_GetValue( NPP instance, NPPVariable variable, void *value );
NPError     Private_SetValue( NPP instance, NPPVariable variable, void *value );
NPError     Private_NewStream(NPP instance, NPMIMEType type, NPStream* stream, NPBool seekable, uint16* stype);
NPError     Private_DestroyStream(NPP instance, NPStream* stream, NPError reason);
int32       Private_WriteReady(NPP instance, NPStream* stream);
int32       Private_Write(NPP instance, NPStream* stream, int32 offset, int32 len, void* buffer);
void        Private_StreamAsFile(NPP instance, NPStream* stream, const char* fname);
void        Private_Print(NPP instance, NPPrint* platformPrint);
int16       Private_HandleEvent(NPP instance, void* event);
void        Private_URLNotify(NPP instance, const char* url, NPReason reason, void* notifyData);

#if 0
jobject     Private_GetJavnp_class();
#endif


NPError Private_Initialize()
{
    NPError err;
    
#if 1
    log_function();
#endif
    err = NPP_Initialize();
    
    return err;
}

void Private_Shutdown()
{
    
#if 1
    log_function();
#endif
    NPP_Shutdown();
    
}

NPError Private_New(NPMIMEType pluginType, NPP instance, uint16 mode, int16 argc, char* argn[], char* argv[], NPSavedData* saved)
{
    
    NPError ret = NPP_New(pluginType, instance, mode, argc, argn, argv, saved);
#if 1
    log_function();
#endif
    
    return ret;
}

NPError Private_Destroy(NPP instance, NPSavedData** save)
{
    NPError err;
    
#if 1
    log_function();
#endif
    err = NPP_Destroy(instance, save);
    
    return err;
}

NPError Private_SetWindow(NPP instance, NPWindow* window)
{
    NPError err;
    
#if 0
    log_function();
#endif
    err = NPP_SetWindow(instance, window);
    
    return err;
}

NPError Private_GetValue(NPP instance, NPPVariable variable, void * value )
{
    NPError err;
    
#if 1
    log_function();
#endif
    err = NPP_GetValue(instance, variable, value);
    
    return err;
}

NPError Private_SetValue( NPP instance, NPNVariable variable, void *value )
{
    NPError err;
    
#if 1
    log_function();
#endif
    err = NPP_SetValue(instance, variable, value);
    
    return err;
}

NPError Private_NewStream(NPP instance, NPMIMEType type, NPStream* stream, NPBool seekable, uint16* stype)
{
    NPError err;
    
#if 1
    log_function();
#endif
    err = NPP_NewStream(instance, type, stream, seekable, stype);
    
    return err;
}

int32 Private_WriteReady(NPP instance, NPStream* stream)
{
    int32 result;
    
#if 0
    log_function();
#endif
    result = NPP_WriteReady(instance, stream);
    
    return result;
}

int32 Private_Write(NPP instance, NPStream* stream, int32 offset, int32 len, void* buffer)
{
    int32 result;
    
#if 0
    log_function();
#endif
    result = NPP_Write(instance, stream, offset, len, buffer);
    
    return result;
}

void Private_StreamAsFile(NPP instance, NPStream * stream, const char * fname)
{
    
    
#if 1
    log_function();
#endif

    // We can stream data from the browser here.
    if (stream && fname)
    {
        // log_debug("stream = " << stream << ", fname = " << fname);
    }

    NPP_StreamAsFile(instance, stream, fname);

    
}

NPError Private_DestroyStream(NPP instance, NPStream * stream, NPError reason)
{
    NPError err;
    
#if 1
    log_function();
#endif
    err = NPP_DestroyStream(instance, stream, reason);
    
    return err;
}

int16 Private_HandleEvent(NPP instance, void * event)
{
    int16 result;
    
#if 0 // too chatty
    log_function();
#endif
    result = NPP_HandleEvent(instance, event);
    
    return result;
}

void Private_Print(NPP instance, NPPrint * platformPrint)
{
    
#if 1
    log_function();
#endif
    NPP_Print(instance, platformPrint);
    
}

void Private_URLNotify(NPP instance, const char * url, NPReason reason, void * notifyData)
{
    
#if 1
    log_function();
#endif
    NPP_URLNotify(instance, url, reason, notifyData);
    
}

#ifdef OJI
jobject Private_GetJavnp_class()
{
    
#if 1
    log_function();
#endif

    jobject clazz = NPP_GetJavnp_class();
    
    if (clazz)
    {
        JRIEnv* env = NPN_GetJavaEnv();
        return (jobject)JRI_NewGlobalRef(env, clazz);
    }
    return 0;
}
#endif

void SetUpQD();
void SetUpQD()
{
#if !TARGET_API_MAC_CARBON
    ProcessSerialNumber PSN;
    FSSpec  myFSSpec;
    Str63   name;
    ProcessInfoRec      infoRec;
    OSErr   result = noErr;
    CFragConnectionID   connID;
    Str255  errName;
#endif

    //
    // Memorize the plugin’s resource file
    // refnum for later use.
    //
    gResFile = CurResFile();

#if !TARGET_API_MAC_CARBON
    //
    // Ask the system if CFM is available.
    //
    long response;
    OSErr err = Gestalt(gestaltCFMAttr, &response);
    Boolean hasCFM = BitTst(&response, 31 - gestaltCFMPresent);

    ProcessInfoRec infoRec;
    
    if (hasCFM)
    {
        //
        // GetProcessInformation takes a process serial number and
        // will give us back the name and FSSpec of the application.
        // See the Process Manager in IM.
        //
        Str63 name;
        FSSpec myFSSpec;
        infoRec.processInfoLength = sizeof(ProcessInfoRec);
        infoRec.processName = name;
        infoRec.processAppSpec = &myFSSpec;

        ProcessSerialNumber PSN;
        PSN.highLongOfPSN = 0;
        PSN.lowLongOfPSN = kCurrentProcess;

        result = GetProcessInformation(&PSN, &infoRec);
        if (result != noErr)
            log_debug("Failed in GetProcessInformation");
    }
    else
        //
        // If no CFM installed, assume it must be a 68K app.
        //
        result = -1;

    CFragConnectionID connID;
    if (result == noErr)
    {
        //
        // Now that we know the app name and FSSpec, we can call GetDiskFragment
        // to get a connID to use in a subsequent call to FindSymbol (it will also
        // return the address of “main” in app, which we ignore).  If GetDiskFragment
        // returns an error, we assume the app must be 68K.
        //
        Ptr mainAddr;
        Str255 errName;
        result =  GetDiskFragment(infoRec.processAppSpec, 0L, 0L, infoRec.processName,
          kLoadCFrag, &connID, (Ptr*)&mainAddr, errName);
    }

    if (result == noErr)
    {
        //
        // The app is a PPC code fragment, so call FindSymbol
        // to get the exported “qd” symbol so we can access its
        // QuickDraw globals.
        //
        CFragSymbolClass symClass;
        result = FindSymbol(connID, "\pqd", (Ptr*)&gQDPtr, &symClass);
        if (result != noErr) {  // this fails if we are in NS 6
            gQDPtr = &qd;       // so we default to the standard QD globals
        }
    }
    else
    {
        //
        // The app is 68K, so use its A5 to compute the address
        // of its QuickDraw globals.
        //
        gQDPtr = (QDGlobals*)(*((long*)SetCurrentA5()) - (sizeof(QDGlobals) - sizeof(GrafPtr)));
    }
#endif
}


#ifdef __GNUC__
// gcc requires that main have an 'int' return type
int main(NPNetscapeFuncs* nsTable, NPPluginFuncs* plugin_funcs, NPP_ShutdownUPP* unloadUpp);
#else
NPError main(NPNetscapeFuncs* nsTable, NPPluginFuncs* plugin_funcs, NPP_ShutdownUPP* unloadUpp);
#endif

#if !TARGET_API_MAC_CARBON
#pragma export on

#if TARGET_RT_MAC_CFM

RoutineDescriptor mainRD = BUILD_ROUTINE_DESCRIPTOR(uppNPP_MainEntryProcInfo, main);

#endif

#pragma export off
#endif /* !TARGET_API_MAC_CARBON */

#ifdef __GNUC__
DEFINE_API_C(int) main(NPNetscapeFuncs* nsTable, NPPluginFuncs* plugin_funcs, NPP_ShutdownUPP* unloadUpp)
#else
DEFINE_API_C(NPError) main(NPNetscapeFuncs* nsTable, NPPluginFuncs* plugin_funcs, NPP_ShutdownUPP* unloadUpp)
#endif
{
    
    log_debug("main");

    NPError err = NPERR_NO_ERROR;

    if ((nsTable == 0) || (plugin_funcs == 0) || (unloadUpp == 0))
    {
        err = NPERR_INVALID_FUNCTABLE_ERROR;
    }

    //
    // Check the “major” version passed in Netscape’s function table.
    // We won’t load if the major version is newer than what we expect.
    // Also check that the function tables passed in are big enough for
    // all the functions we need (they could be bigger, if Netscape added
    // new APIs, but that’s OK with us -- we’ll just ignore them).
    //
    if (err == NPERR_NO_ERROR)
    {
        if ((nsTable->version >> 8) > NP_VERSION_MAJOR)     // Major version is in high byte
            err = NPERR_INCOMPATIBLE_VERSION_ERROR;
    }

    if (err == NPERR_NO_ERROR)
    {
        //
        // Copy all the fields of Netscape’s function table into our
        // copy so we can call back into Netscape later.  Note that
        // we need to copy the fields one by one, rather than assigning
        // the whole structure, because the Netscape function table
        // could actually be bigger than what we expect.
        //

        int version_minor = nsTable->version & 0xFF;

        g_ns_function_procs.version          = nsTable->version;
        g_ns_function_procs.size = nsTable->size;
        g_ns_function_procs.posturl          = (NPN_PostURLUPP)HOST_TO_PLUGIN_GLUE(posturl, nsTable->posturl);
        g_ns_function_procs.geturl           = (NPN_GetURLUPP)HOST_TO_PLUGIN_GLUE(geturl, nsTable->geturl);
        g_ns_function_procs.requestread      = (NPN_RequestReadUPP)HOST_TO_PLUGIN_GLUE(requestread, nsTable->requestread);
        g_ns_function_procs.newstream        = (NPN_NewStreamUPP)HOST_TO_PLUGIN_GLUE(newstream, nsTable->newstream);
        g_ns_function_procs.write            = (NPN_WriteUPP)HOST_TO_PLUGIN_GLUE(write, nsTable->write);
        g_ns_function_procs.destroystream    = (NPN_DestroyStreamUPP)HOST_TO_PLUGIN_GLUE(destroystream, nsTable->destroystream);
        g_ns_function_procs.status           = (NPN_StatusUPP)HOST_TO_PLUGIN_GLUE(status, nsTable->status);
        g_ns_function_procs.uagent           = (NPN_UserAgentUPP)HOST_TO_PLUGIN_GLUE(uagent, nsTable->uagent);
        g_ns_function_procs.memalloc         = (NPN_MemAllocUPP)HOST_TO_PLUGIN_GLUE(memalloc, nsTable->memalloc);
        g_ns_function_procs.memfree          = (NPN_MemFreeUPP)HOST_TO_PLUGIN_GLUE(memfree, nsTable->memfree);
        g_ns_function_procs.memflush         = (NPN_MemFlushUPP)HOST_TO_PLUGIN_GLUE(memflush, nsTable->memflush);
        g_ns_function_procs.reloadplugins    = (NPN_ReloadPluginsUPP)HOST_TO_PLUGIN_GLUE(reloadplugins, nsTable->reloadplugins);
        if (version_minor >= NPVERS_HAS_LIVECONNECT )
        {
            g_ns_function_procs.getJavaEnv   = (NPN_GetJavaEnvUPP)HOST_TO_PLUGIN_GLUE(getJavaEnv, nsTable->getJavaEnv);
            g_ns_function_procs.getJavaPeer  = (NPN_GetJavaPeerUPP)HOST_TO_PLUGIN_GLUE(getJavaPeer, nsTable->getJavaPeer);
        }
        if (version_minor >= NPVERS_HAS_NOTIFICATION )
        {
            g_ns_function_procs.geturlnotify = (NPN_GetURLNotifyUPP)HOST_TO_PLUGIN_GLUE(geturlnotify, nsTable->geturlnotify);
            g_ns_function_procs.posturlnotify    = (NPN_PostURLNotifyUPP)HOST_TO_PLUGIN_GLUE(posturlnotify, nsTable->posturlnotify);
        }
        g_ns_function_procs.getvalue         = (NPN_GetValueUPP)HOST_TO_PLUGIN_GLUE(getvalue, nsTable->getvalue);
        g_ns_function_procs.setvalue         = (NPN_SetValueUPP)HOST_TO_PLUGIN_GLUE(setvalue, nsTable->setvalue);
        g_ns_function_procs.invalidaterect   = (NPN_InvalidateRectUPP)HOST_TO_PLUGIN_GLUE(invalidaterect, nsTable->invalidaterect);
        g_ns_function_procs.invalidateregion = (NPN_InvalidateRegionUPP)HOST_TO_PLUGIN_GLUE(invalidateregion, nsTable->invalidateregion);
        g_ns_function_procs.forceredraw      = (NPN_ForceRedrawUPP)HOST_TO_PLUGIN_GLUE(forceredraw, nsTable->forceredraw);
        if (version_minor >= 14 )
        {
            // NPRuntime support
            g_ns_function_procs.getstringidentifier  = (NPN_GetStringIdentifierUPP)HOST_TO_PLUGIN_GLUE(getstringidentifier, nsTable->getstringidentifier);
            g_ns_function_procs.getstringidentifiers = (NPN_GetStringIdentifiersUPP)HOST_TO_PLUGIN_GLUE(getstringidentifiers, nsTable->getstringidentifiers);
            g_ns_function_procs.getintidentifier     = (NPN_GetIntIdentifierUPP)HOST_TO_PLUGIN_GLUE(getintidentifier, nsTable->getintidentifier);
            g_ns_function_procs.identifierisstring   = (NPN_IdentifierIsStringUPP)HOST_TO_PLUGIN_GLUE(identifierisstring, nsTable->identifierisstring);
            g_ns_function_procs.utf8fromidentifier   = (NPN_UTF8FromIdentifierUPP)HOST_TO_PLUGIN_GLUE(utf8fromidentifier, nsTable->utf8fromidentifier);
            g_ns_function_procs.intfromidentifier    = (NPN_IntFromIdentifierUPP)HOST_TO_PLUGIN_GLUE(intfromidentifier, nsTable->intfromidentifier);
            g_ns_function_procs.createobject         = (NPN_CreateObjectUPP)HOST_TO_PLUGIN_GLUE(createobject, nsTable->createobject);
            g_ns_function_procs.retainobject         = (NPN_RetainObjectUPP)HOST_TO_PLUGIN_GLUE(retainobject, nsTable->retainobject);
            g_ns_function_procs.releaseobject        = (NPN_ReleaseObjectUPP)HOST_TO_PLUGIN_GLUE(releaseobject, nsTable->releaseobject);
            g_ns_function_procs.invoke   = (NPN_InvokeUPP)HOST_TO_PLUGIN_GLUE(invoke, nsTable->invoke);
            g_ns_function_procs.invokeDefault        = (NPN_InvokeDefaultUPP)HOST_TO_PLUGIN_GLUE(invokeDefault, nsTable->invokeDefault);
            g_ns_function_procs.evaluate = (NPN_EvaluateUPP)HOST_TO_PLUGIN_GLUE(evaluate, nsTable->evaluate);
            g_ns_function_procs.getproperty          = (NPN_GetPropertyUPP)HOST_TO_PLUGIN_GLUE(getproperty, nsTable->getproperty);
            g_ns_function_procs.setproperty          = (NPN_SetPropertyUPP)HOST_TO_PLUGIN_GLUE(setproperty, nsTable->setproperty);
            g_ns_function_procs.removeproperty       = (NPN_RemovePropertyUPP)HOST_TO_PLUGIN_GLUE(removeproperty, nsTable->removeproperty);
            g_ns_function_procs.hasproperty          = (NPN_HasPropertyUPP)HOST_TO_PLUGIN_GLUE(hasproperty, nsTable->hasproperty);
            g_ns_function_procs.hasmethod            = (NPN_HasMethodUPP)HOST_TO_PLUGIN_GLUE(hasmethod, nsTable->hasmethod);
            g_ns_function_procs.releasevariantvalue  = (NPN_ReleaseVariantValueUPP)HOST_TO_PLUGIN_GLUE(releasevariantvalue, nsTable->releasevariantvalue);
            g_ns_function_procs.setexception         = (NPN_SetExceptionUPP)HOST_TO_PLUGIN_GLUE(setexception, nsTable->setexception);
        }

        //
        // Set up the plugin function table that Netscape will use to
        // call us.  Netscape needs to know about our version and size
        // and have a UniversalProcPointer for every function we implement.
        //
        plugin_funcs->version        = (NP_VERSION_MAJOR << 8) + NP_VERSION_MINOR;
        plugin_funcs->size           = sizeof(NPPluginFuncs);
        plugin_funcs->newp           = NewNPP_NewProc(PLUGIN_TO_HOST_GLUE(newp, Private_New));
        plugin_funcs->destroy        = NewNPP_DestroyProc(PLUGIN_TO_HOST_GLUE(destroy, Private_Destroy));
        plugin_funcs->setwindow      = NewNPP_SetWindowProc(PLUGIN_TO_HOST_GLUE(setwindow, Private_SetWindow));
        plugin_funcs->newstream      = NewNPP_NewStreamProc(PLUGIN_TO_HOST_GLUE(newstream, Private_NewStream));
        plugin_funcs->destroystream  = NewNPP_DestroyStreamProc(PLUGIN_TO_HOST_GLUE(destroystream, Private_DestroyStream));
        plugin_funcs->asfile         = NewNPP_StreamAsFileProc(PLUGIN_TO_HOST_GLUE(asfile, Private_StreamAsFile));
        plugin_funcs->writeready     = NewNPP_WriteReadyProc(PLUGIN_TO_HOST_GLUE(writeready, Private_WriteReady));
        plugin_funcs->write          = NewNPP_WriteProc(PLUGIN_TO_HOST_GLUE(write, Private_Write));
        plugin_funcs->print          = NewNPP_PrintProc(PLUGIN_TO_HOST_GLUE(print, Private_Print));
        plugin_funcs->event          = NewNPP_HandleEventProc(PLUGIN_TO_HOST_GLUE(event, Private_HandleEvent));
        plugin_funcs->getvalue       = NewNPP_GetValueProc(PLUGIN_TO_HOST_GLUE(getvalue, Private_GetValue));
        if (version_minor >= NPVERS_HAS_NOTIFICATION )
        {
            plugin_funcs->urlnotify = NewNPP_URLNotifyProc(PLUGIN_TO_HOST_GLUE(urlnotify, Private_URLNotify));
        }
#ifdef OJI
        if (version_minor >= NPVERS_HAS_LIVECONNECT )
        {
            plugin_funcs->javnp_class  = (JRIGlobalRef)Private_GetJavnp_class();
        }
#else
        plugin_funcs->javaClass = 0;
#endif
        *unloadUpp = NewNPP_ShutdownProc(PLUGIN_TO_HOST_GLUE(
            shutdown, Private_Shutdown)
        );

        SetUpQD();
        err = Private_Initialize();
    }

    
    return err;
}

#if defined(__MACH__)

/*
** netscape plugins functions when building Mach-O binary
*/
#pragma export on

extern "C"
{
    NPError NP_Initialize(NPNetscapeFuncs * nsTable);
    NPError NP_GetEntryPoints(NPPluginFuncs * plugin_funcs);
    void NP_Shutdown();
}

#pragma export off

/*
** netscape plugins functions when using Mach-O binary
*/

NPError NP_Initialize(NPNetscapeFuncs * nsTable)
{
    log_function();
    
    if (0 == nsTable)
    {
        log_debug("***Error: NPERR_INVALID_FUNCTABLE_ERROR");
        return NPERR_INVALID_FUNCTABLE_ERROR;
    }

    /*
     * Check the major version passed in Netscape's function table.
     * We won't load if the major version is newer than what we expect.
     * Also check that the function tables passed in are big enough for
     * all the functions we need (they could be bigger, if Netscape added
     * new APIs, but that's OK with us -- we'll just ignore them).
     *
     */

    if ((nsTable->version >> 8) > NP_VERSION_MAJOR)
    {
        log_debug("***Error: NPERR_INCOMPATIBLE_VERSION_ERROR");
        return NPERR_INCOMPATIBLE_VERSION_ERROR;
    }
    
    nsTable->size = sizeof(NPPluginFuncs);
#if defined(__APPLE__)
    // :!!!:WebKit bug:julian:20081204 
#else
    if (nsTable->size < sizeof(NPPluginFuncs))
    {
        log_debug("***Error: NPERR_INVALID_FUNCTABLE_ERROR");
        
        return NPERR_INVALID_FUNCTABLE_ERROR;
    }
#endif
    int version_minor = nsTable->version & 0xFF;

    /*
     * Copy all the fields of Netscape function table into our
     * copy so we can call back into Netscape later.  Note that
     * we need to copy the fields one by one, rather than assigning
     * the whole structure, because the Netscape function table
     * could actually be bigger than what we expect.
     */
    g_ns_function_procs.version = nsTable->version;
    g_ns_function_procs.size = nsTable->size;
    g_ns_function_procs.posturl = nsTable->posturl;
    g_ns_function_procs.geturl = nsTable->geturl;
    g_ns_function_procs.requestread = nsTable->requestread;
    g_ns_function_procs.newstream = nsTable->newstream;
    g_ns_function_procs.write = nsTable->write;
    g_ns_function_procs.destroystream = nsTable->destroystream;
    g_ns_function_procs.status = nsTable->status;
    g_ns_function_procs.uagent = nsTable->uagent;
    g_ns_function_procs.memalloc = nsTable->memalloc;
    g_ns_function_procs.memfree = nsTable->memfree;
    g_ns_function_procs.memflush = nsTable->memflush;
    g_ns_function_procs.reloadplugins = nsTable->reloadplugins;
    
    if (version_minor >= NPVERS_HAS_LIVECONNECT )
    {
        log_debug("Info: NPVERS_HAS_LIVECONNECT");
        
        g_ns_function_procs.getJavaEnv = nsTable->getJavaEnv;
        g_ns_function_procs.getJavaPeer = nsTable->getJavaPeer;
    }
    if (version_minor >= NPVERS_HAS_NOTIFICATION )
    {
        log_debug("Info: NPVERS_HAS_LIVECONNECT");
        
        g_ns_function_procs.geturlnotify = nsTable->geturlnotify;
        g_ns_function_procs.posturlnotify = nsTable->posturlnotify;
    }

    g_ns_function_procs.getvalue = nsTable->getvalue;
    g_ns_function_procs.setvalue = nsTable->setvalue;
    g_ns_function_procs.invalidaterect = nsTable->invalidaterect;
    g_ns_function_procs.invalidateregion = nsTable->invalidateregion;
    g_ns_function_procs.forceredraw = nsTable->forceredraw;
    
    if (version_minor >= 14)
    {
        // NPRuntime support
        g_ns_function_procs.getstringidentifier = nsTable->getstringidentifier;
        g_ns_function_procs.getstringidentifiers = nsTable->getstringidentifiers;
        g_ns_function_procs.getintidentifier = nsTable->getintidentifier;
        g_ns_function_procs.identifierisstring = nsTable->identifierisstring;
        g_ns_function_procs.utf8fromidentifier = nsTable->utf8fromidentifier;
        g_ns_function_procs.intfromidentifier = nsTable->intfromidentifier;
        g_ns_function_procs.createobject = nsTable->createobject;
        g_ns_function_procs.retainobject = nsTable->retainobject;
        g_ns_function_procs.releaseobject = nsTable->releaseobject;
        g_ns_function_procs.invoke = nsTable->invoke;
        g_ns_function_procs.invokeDefault = nsTable->invokeDefault;
        g_ns_function_procs.evaluate = nsTable->evaluate;
        g_ns_function_procs.getproperty = nsTable->getproperty;
        g_ns_function_procs.setproperty = nsTable->setproperty;
        g_ns_function_procs.removeproperty = nsTable->removeproperty;
        g_ns_function_procs.hasproperty = nsTable->hasproperty;
        g_ns_function_procs.hasmethod = nsTable->hasmethod;
        g_ns_function_procs.releasevariantvalue = nsTable->releasevariantvalue;
        g_ns_function_procs.setexception = nsTable->setexception;
    }
    return NPP_Initialize();
}

NPError NP_GetEntryPoints(NPPluginFuncs * plugin_funcs)
{
    int version_minor = g_ns_function_procs.version & 0xFF;

    if (plugin_funcs == 0)
    {
        log_debug("NPERR_INVALID_FUNCTABLE_ERROR");
        
        return NPERR_INVALID_FUNCTABLE_ERROR;
    }

    /*if (plugin_funcs->size < sizeof(NPPluginFuncs))
    return NPERR_INVALID_FUNCTABLE_ERROR;*/

    /*
     * Set up the plugin function table that Netscape will use to
     * call us.  Netscape needs to know about our version and size
     * and have a UniversalProcPointer for every function we
     * implement.
     */

    plugin_funcs->version    = (NP_VERSION_MAJOR << 8) + NP_VERSION_MINOR;
    plugin_funcs->size       = sizeof(NPPluginFuncs);
    plugin_funcs->newp       = NewNPP_NewProc(Private_New);
    plugin_funcs->destroy    = NewNPP_DestroyProc(Private_Destroy);
    plugin_funcs->setwindow  = NewNPP_SetWindowProc(Private_SetWindow);
    plugin_funcs->newstream  = NewNPP_NewStreamProc(Private_NewStream);
    plugin_funcs->destroystream = NewNPP_DestroyStreamProc(Private_DestroyStream);
    plugin_funcs->asfile     = NewNPP_StreamAsFileProc(Private_StreamAsFile);
    plugin_funcs->writeready = NewNPP_WriteReadyProc(Private_WriteReady);
    plugin_funcs->write      = NewNPP_WriteProc(Private_Write);
    plugin_funcs->print      = NewNPP_PrintProc(Private_Print);
    plugin_funcs->event      = NewNPP_HandleEventProc(Private_HandleEvent);
    plugin_funcs->getvalue   = NewNPP_GetValueProc(Private_GetValue);
    plugin_funcs->setvalue   = NewNPP_SetValueProc(Private_SetValue);
    
    if (version_minor >= NPVERS_HAS_NOTIFICATION)
    {
        plugin_funcs->urlnotify = Private_URLNotify;
    }
#ifdef OJI
    if (version_minor >= NPVERS_HAS_LIVECONNECT)
    {
        plugin_funcs->javaClass  = (JRIGlobalRef)Private_GetJavnp_class();
    }
#else
    plugin_funcs->javaClass = 0;
#endif

    return NPERR_NO_ERROR;
}

void NP_Shutdown()
{
    log_debug("NP_Shutdown");
    NPP_Shutdown();
}

#endif
