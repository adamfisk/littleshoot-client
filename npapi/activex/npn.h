
#pragma once

#include "npapi.h"
#include "npupp.h"

#define NP_EXPORT

NPError NP_EXPORT NPN_RequestRead(NPStream * pstream, NPByteRange * rangeList);

NPError NP_EXPORT NPN_GetURLNotify(
    NPP npp, const char* relativeURL, const char * target, void * notifyData
);

NPError NP_EXPORT NPN_GetValue(NPP npp, NPNVariable variable, void * r_value);

NPError NP_EXPORT NPN_SetValue(NPP npp, NPPVariable variable, void * r_value);

NPError NP_EXPORT NPN_GetURL(
    NPP npp, const char * relativeURL, const char * target
);

NPError NP_EXPORT NPN_PostURLNotify(
    NPP npp, const char * relativeURL, const char * target, uint32 len, 
    const char * buf, NPBool file, void * notifyData
);

NPError NP_EXPORT NPN_PostURL(
    NPP npp, const char * relativeURL, const char *target, uint32 len, 
    const char * buf, NPBool file
);

NPError NP_EXPORT NPN_NewStream(
    NPP npp, NPMIMEType type, const char * window, NPStream ** pstream
);

int32 NP_EXPORT NPN_Write(NPP npp, NPStream *pstream, int32 len, void * buffer);

NPError NP_EXPORT NPN_DestroyStream(NPP npp, NPStream *pstream, NPError reason);

void NP_EXPORT NPN_Status(NPP npp, const char *message);

void * NP_EXPORT NPN_MemAlloc (uint32 size);

void NP_EXPORT NPN_MemFree (void * ptr);

uint32 NP_EXPORT NPN_MemFlush(uint32 size);

void NP_EXPORT NPN_ReloadPlugins(NPBool reloadPages);

void NP_EXPORT NPN_InvalidateRect(NPP npp, NPRect * invalidRect);

void NP_EXPORT NPN_InvalidateRegion(NPP npp, NPRegion invalidRegion);

const char * NP_EXPORT NPN_UserAgent(NPP npp);

JRIEnv * NP_EXPORT NPN_GetJavaEnv(void);

jref NP_EXPORT NPN_GetJavaPeer(NPP npp);

java_lang_Class * NP_EXPORT NPN_GetJavaClass(void * handle);

void NP_EXPORT NPN_ForceRedraw(NPP npp);

